package com.example.catnicwarehouse.Inventory.comment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemViewState
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.viewModel.AmountItemInventoryViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentCommentInventorsBinding
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.catnicwarehouse.utils.updateWithTextWatcher
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comment_inventors.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CommentFragmentInventors : BaseFragment() {

    private var _binding: FragmentCommentInventorsBinding? = null
    private val binding get() = _binding!!
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val amountItemViewModel: AmountItemInventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentInventorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        displayComment()
        setupSaveButton()
        observeCommentEvents()
    }

    private fun handleBackButton() {
        binding.commentHeader.headerTitle.text = getString(R.string.note)
        binding.commentHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeCommentEvents() {
        amountItemViewModel.amountItemFlow.onEach { state ->
            when (state) {
                AmountItemViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is AmountItemViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is AmountItemViewState.InventoryItemUpdated -> {
                    progressBarManager.dismiss()
                    findNavController().popBackStack()
                }

                AmountItemViewState.Loading -> progressBarManager.show()
                AmountItemViewState.Reset -> progressBarManager.dismiss()
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun displayComment() {
        val itemId = inventorySharedViewModel.itemId
        val savedComment = itemId?.let { inventorySharedViewModel.updatedItemComment[it] }
        val commentToShow = savedComment ?: inventorySharedViewModel.clickedArticleComment

        binding.commentText.setText(if (commentToShow.isNullOrEmpty() || commentToShow == "string") "" else commentToShow)
    }

    private fun setupSaveButton() {
        updateSaveButtonState(binding.commentText.text?.isNotEmpty() ?: false)

        binding.commentText.addTextChangedListener {
            updateSaveButtonState(it?.toString()?.isNotEmpty() ?: false)
        }

        binding.saveButton.setOnClickListener {
            val itemId = inventorySharedViewModel.itemId
            val comment = binding.commentText.text.toString()
            if (itemId != null) {
                inventorySharedViewModel.saveUpdatedComment(itemId, comment)
                amountItemViewModel.onEvent(
                    AmountItemEvent.UpdateInventoryItem(
                        stockyardId = inventorySharedViewModel.clickedStockyardId,
                        itemId = itemId,
                        setInventoryItems = SetInventoryItems(
                            actualStock = itemId.let { inventorySharedViewModel.updatedItemAmount[it].takeIf { it != -1 } }
                                ?: inventorySharedViewModel.clickedItemIdActualStock,
                            actualUnitCode = itemId.let { inventorySharedViewModel.updatedActualUnitCode[it] }
                                ?: inventorySharedViewModel.unitCodeActual
                                ?: inventorySharedViewModel.inventoryItem?.actualUnitCode,
                            comment = inventorySharedViewModel.updatedItemComment[itemId]
                                ?: inventorySharedViewModel.inventoryItem?.comment
                        )
                    )
                )
            }
        }
    }

    private fun updateSaveButtonState(isEnabled: Boolean) {
        val drawableRes = if (isEnabled) {
            R.drawable.orange_rounded_button
        } else {
            R.drawable.grey_rounded_button
        }
        binding.saveButton.setBackgroundResource(drawableRes)
        binding.saveButton.setTextColor(
            if (isEnabled) Color.WHITE else ContextCompat.getColor(
                requireContext(),
                R.color.disabled_button_text_color
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        amountItemViewModel.onEvent(AmountItemEvent.Empty)
    }
}