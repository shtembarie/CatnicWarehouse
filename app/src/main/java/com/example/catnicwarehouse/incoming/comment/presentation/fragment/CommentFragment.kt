package com.example.catnicwarehouse.incoming.comment.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentCommentBinding
import com.example.catnicwarehouse.incoming.comment.presentation.sealedClass.CommentEvent
import com.example.catnicwarehouse.incoming.comment.presentation.sealedClass.CommentViewState
import com.example.catnicwarehouse.incoming.comment.presentation.viewModel.CommentViewModel

import com.example.catnicwarehouse.shared.presentation.enums.CommentType
import com.example.catnicwarehouse.shared.presentation.enums.CommentType.*
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.utils.updateWithTextWatcher
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CommentFragment : BaseFragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val viewModel: CommentViewModel by viewModels()
    private var commentType: CommentType = DEFECTIVE
    private var comment:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentTypeString = arguments?.getString("commentType")
        comment = arguments?.getString("comment")
        commentType = CommentType.valueOf(commentTypeString ?: DEFECTIVE.name)

        handleUIBasedOnType()
        initView(comment)
        observeCommentEvents()
        handleBackButton()
        handleSaveButton()
    }

    private fun observeCommentEvents() {
        viewModel.commentFlow.onEach { state ->
            when (state) {
                is CommentViewState.DeliveryNoteSaved -> {
                    progressBarManager.dismiss()
                    if (state.isNoteSaved == true) {
                        sharedViewModel.onEvents(SharedEvent.UpdateDeliveryNote(binding.commentText.text.toString().trim()))
                        findNavController().popBackStack()
                    }
                }

                CommentViewState.Empty -> progressBarManager.dismiss()
                is CommentViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                CommentViewState.Loading -> progressBarManager.show()
                CommentViewState.Reset -> progressBarManager.dismiss()
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleUIBasedOnType() {
        when (commentType) {
            DEFECTIVE -> {
                binding.commentHeader.headerTitle.text = getString(R.string.comments)
                binding.articleCodeTextInput.hint = getString(R.string.type_your_comment)
            }

            DELIVERY_NOTE -> {
                binding.commentHeader.headerTitle.text = getString(R.string.note)
                binding.articleCodeTextInput.hint = getString(R.string.comment)
            }
        }
    }

    private fun initView(selectedDefectiveComment: String?) {
        with(binding) {
            commentText.setText(selectedDefectiveComment ?: "")
            if (commentText.text.isNullOrEmpty())
                handleSaveButtonOnTextChange(buttonEnabled = false)
            else
                handleSaveButtonOnTextChange(buttonEnabled = true)
        }
    }


    private fun handleSaveButton() {
        with(binding) {
            commentText.updateWithTextWatcher { comment ->
                if (comment.isEmpty())
                    handleSaveButtonOnTextChange(buttonEnabled = false)
                else
                    handleSaveButtonOnTextChange(buttonEnabled = true)
            }

            saveButton.setOnClickListener {
                val comment = commentText.text.toString().trim()
                when (commentType) {
                    DEFECTIVE -> {
                        parentFragmentManager.setFragmentResult(
                            "handleDefectiveCommentUpdate",
                            bundleOf(
                                "comment" to comment,
                                "commentType" to commentType.name
                            )
                        )
                        findNavController().popBackStack()
                    }


                    DELIVERY_NOTE -> {
                        val noteRequestModel = DeliveryNoteRequestModel(comment)
                        val deliveryId = sharedViewModel.getDeliveryId()?:""

                        viewModel.onEvent(
                            CommentEvent.SaveDeliveryNote(
                                deliveryId,
                                noteRequestModel
                            )
                        )
                    }
                }


            }
        }
    }

    private fun handleSaveButtonOnTextChange(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.saveButton.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
        }

    }

    private fun handleBackButton() {
        binding.commentHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}