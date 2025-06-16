package com.example.catnicwarehouse.inventoryNew.comment.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentComment2Binding
import com.example.catnicwarehouse.databinding.FragmentInventoryArticleDescriptionBinding
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentEvent
import com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses.InventoryCommentViewState
import com.example.catnicwarehouse.inventoryNew.comment.presentation.viewModel.CommentViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CommentFragment : BaseFragment() {

    private var _binding: FragmentComment2Binding? = null
    private val binding get() = _binding!!

    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComment2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        populateUIWithData()
        handleButtonClickActions()
        observeCommentEvents()

        if (binding.commentText.text.toString().isBlank().not()) {
            handleSaveButtonOnTextChange(buttonEnabled = true)
        } else {
            handleSaveButtonOnTextChange(buttonEnabled = false)
        }


        binding.commentText.addTextChangedListener { editable ->
            if (editable?.length != 0) {
                handleSaveButtonOnTextChange(buttonEnabled = true)
            } else {
                handleSaveButtonOnTextChange(buttonEnabled = false)
            }
        }

    }

    private fun observeCommentEvents() {
        commentViewModel.commentFlow.onEach { state ->
            when (state) {
                is InventoryCommentViewState.CommentUpdated -> {
                    progressBarManager.dismiss()
                    inventorySharedViewModel.selectedInventoryItem?.comment = state.comment ?: ""
                    findNavController().popBackStack()
                }

                InventoryCommentViewState.Empty -> progressBarManager.dismiss()
                is InventoryCommentViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(it) }
                }

                InventoryCommentViewState.Loading -> {
                    progressBarManager.show()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleButtonClickActions() {
        binding.saveButton.setOnClickListener {
            val note = binding.commentText.text.toString()
            // In case an existing inventory item is selected
            if (inventorySharedViewModel.selectedInventoryItem != null) {
                commentViewModel.onEvent(
                    InventoryCommentEvent.UpdateComment(
                        id = inventorySharedViewModel.selectedInventoryId.toString(),
                        itemId = inventorySharedViewModel.selectedInventoryItem?.id.toString(),
                        deliveryNoteRequestModel = DeliveryNoteRequestModel(note = note)
                    )
                )
            } else {// In case a new article is selected
                inventorySharedViewModel.selectedArticle?.comment = note
                findNavController().popBackStack()
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

    private fun populateUIWithData() {
        binding.commentText.setText(
            inventorySharedViewModel.selectedInventoryItem?.comment
                ?: inventorySharedViewModel.selectedArticle?.comment
                ?: ""
        )
    }

    private fun handleHeaderSection() {
        binding.commentHeader.headerTitle.text = getString(R.string.note)
        binding.commentHeader.toolbarSection.visibility = View.VISIBLE
        binding.commentHeader.rightToolbarButton.visibility = View.GONE
        binding.commentHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}