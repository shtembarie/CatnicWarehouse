package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.bottomSheet.UnitTypeBottomSheetInventory
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemViewState
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.viewModel.AmountItemInventoryViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAmountItemInventoryBinding
import com.example.catnicwarehouse.incoming.amountItem.presentation.viewModel.AmountItemViewModel
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.repository.inventory.model.SetInventoryItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AmountItemInventoryFragment : BaseFragment() {
    private var _binding: FragmentAmountItemInventoryBinding? = null
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val binding get() = _binding!!

    private val amountItemViewModel: AmountItemInventoryViewModel by viewModels()
    var unitTypeUpdated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAmountItemInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        displayItemAmount()
        setUpSaveButton()
        setUpChangeUnitTypeButton()
        observeAmountItemUpdateEvents()
    }

    private fun observeAmountItemUpdateEvents() {
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
                    if (!unitTypeUpdated) {
                        val itemId = inventorySharedViewModel.itemId
                        val itemAmountText = binding.amountItemText.text.toString()
                        val itemAmount = itemAmountText.toIntOrNull()
                        if (itemAmount != null) {
                            if (itemId != null) {
                                inventorySharedViewModel.saveUpdatedItemAmount(itemId, itemAmount)
                                findNavController().popBackStack()
                            }
                        }
                    }
                }

                AmountItemViewState.Loading -> progressBarManager.show()
                AmountItemViewState.Reset -> progressBarManager.dismiss()
            }

        }

            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleBackButton() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.counted_in_stock)
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun displayItemAmount() {
        val itemId = inventorySharedViewModel.itemId
        val savedItemAmount = itemId?.let { inventorySharedViewModel.updatedItemAmount[it] }
        val savedItemToShow =
            savedItemAmount.takeIf { it != -1 } ?: inventorySharedViewModel.clickedItemIdActualStock

        binding.amountItemText.setText(if (savedItemToShow == -1) "" else savedItemToShow.toString())

        var value = binding.amountItemText.text.toString().trim()
        if (value.isEmpty()) value = "0"

        // Add focus change listener
        binding.amountItemText.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // Clear the text when EditText is focused
                    if (value.toInt() == 0)
                        binding.amountItemText.setText("")
                    else
                        binding.amountItemText.setText("${(value.toInt())}")

                } else {
                    // Restore the text when focus is lost
                    binding.amountItemText.setText(value)

                }
            }
    }

    private fun setUpSaveButton() {
        updateSaveButtonState(binding.amountItemText.text?.isNotEmpty() ?: false)
        binding.amountItemText.addTextChangedListener {
            updateSaveButtonState(it?.toString()?.isNotEmpty() ?: false)
        }
        binding.saveAmountButton.setOnClickListener {
            saveItemAmount()
        }
        binding.saveButton.setOnClickListener {
            saveItemAmount()
        }
    }

    private fun saveItemAmount() {
        val itemId = inventorySharedViewModel.itemId
        val itemAmountText = binding.amountItemText.text.toString()
        val itemAmount = itemAmountText.toIntOrNull()
        if (itemAmount != null) {
            if (itemId != null) {
                unitTypeUpdated = false
                amountItemViewModel.onEvent(
                    AmountItemEvent.UpdateInventoryItem(
                        stockyardId = inventorySharedViewModel.clickedStockyardId,
                        itemId = itemId,
                        setInventoryItems = SetInventoryItems(
                            actualStock = itemAmountText.toIntOrNull(),
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

    private fun setUpChangeUnitTypeButton() {
        binding.changeUnitTypeLayout.setOnClickListener {
            val unitTypeBottomSheet = UnitTypeBottomSheetInventory()
            unitTypeBottomSheet.setOnUnitTypeSelectedListener { unitType ->
                val itemId = inventorySharedViewModel.itemId
                unitTypeBottomSheet.updateSelectedUnitType(unitType)
                if (itemId != null) {
                    inventorySharedViewModel.saveUpdatedActualUnitCode(itemId, unitType)
                    unitTypeUpdated = true
                    amountItemViewModel.onEvent(
                        AmountItemEvent.UpdateInventoryItem(
                            stockyardId = inventorySharedViewModel.clickedStockyardId,
                            itemId = itemId,
                            setInventoryItems = SetInventoryItems(
                                actualStock = binding.amountItemText.text.toString()
                                    .toIntOrNull(),
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
            unitTypeBottomSheet.show(parentFragmentManager, unitTypeBottomSheet.tag)
        }
    }

    @SuppressLint("ResourceType")
    private fun updateSaveButtonState(isEnabled: Boolean) {
        val drawableRes = if (isEnabled) {
            R.drawable.orange_rounded_button
        } else {
            R.drawable.grey_rounded_button
        }
        binding.saveAmountButton.setBackgroundResource(drawableRes)
        binding.saveButton.setBackgroundResource(drawableRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        amountItemViewModel.onEvent(AmountItemEvent.Empty)
        _binding = null
    }
}