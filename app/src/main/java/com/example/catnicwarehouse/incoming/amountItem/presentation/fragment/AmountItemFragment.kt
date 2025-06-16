package com.example.catnicwarehouse.incoming.amountItem.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.incoming.amountItem.presentation.bottomSheetFragment.UnitMeterTypeBottomSheetFragment
import com.example.catnicwarehouse.databinding.FragmentAmountItem2Binding
import com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment.DefectiveItemInfoBottomSheetFragment
import com.example.catnicwarehouse.shared.presentation.enums.ItemType
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.utils.updateWithNumberTextWatcher
import com.example.shared.networking.network.article.ArticleUnit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmountItemFragment : BaseFragment(), UnitMeterTypeBottomSheetFragment.UnitMeterTypeListener {

    private var _binding: FragmentAmountItem2Binding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private var itemType: ItemType = ItemType.QTY
    private var updatedAmount: String = "0"
    private var updatedAmountUnit: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmountItem2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            updatedAmount = it.getString("amount") ?: "0"
            updatedAmountUnit = it.getString("unit")
            itemType = ItemType.valueOf(it.getString("itemType") ?: ItemType.QTY.name)
        }
        initViews(itemType = itemType)
        handleBackButton()
        handleUnitTypeClickAction()
        handleSaveButtonAction()
        observeSharedEvents()
    }

    private fun observeSharedEvents() {
//        sharedVM.deliverySharedFlow.onEach { state ->
//            when (state) {
//                DeliverySharedViewState.Empty -> progressBarManager.dismiss()
//                is DeliverySharedViewState.Error -> progressBarManager.dismiss()
//                DeliverySharedViewState.Loading -> progressBarManager.show()
//                is DeliverySharedViewState.UpdatedValue -> {
//                    progressBarManager.dismiss()
//                    val shouldUpdateUI =
//                        state.events.none { it is DeliverySharedEvent.UpdateSelectedQty }
//
//                    itemType = state.deliveryLocalModel.itemType
//                    if (shouldUpdateUI) {
//                        val selectedQty = state.deliveryLocalModel.selectedQty
//                        val localDefectiveQty = state.deliveryLocalModel.localDefectiveQty ?: "0"
//
//                        when (val itemType = state.deliveryLocalModel.itemType) {
//                            QTY -> initViews(selectedQty, itemType)
//                            DEFECTIVE -> initViews(localDefectiveQty, itemType)
//                        }
//                    }
//
//
//                }
//
//                is DeliverySharedViewState.NavigateTo -> progressBarManager.dismiss()
//                else -> {
//                    progressBarManager.dismiss()
//                }
//            }
//
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleUnitTypeClickAction() {
        binding.changeUnitTypeLayout.setOnClickListener {
            showUnitMeterTypeBottomSheet()
        }
    }

    private fun handleSaveButtonAction() {
        binding.saveAmountButton.setOnClickListener {
            if (itemType == ItemType.QTY)
                parentFragmentManager.setFragmentResult(
                    "handleAmountAndUnitUpdate",
                    bundleOf(
                        "amount" to updatedAmount,
                        "amountUnit" to updatedAmountUnit,
                        "itemType" to itemType.name
                    )
                )
            else if (itemType == ItemType.DEFECTIVE)
                parentFragmentManager.setFragmentResult(
                    "handleDefectiveAmountAndUnitUpdate",
                    bundleOf(
                        "amount" to updatedAmount,
                        "amountUnit" to updatedAmountUnit,
                        "itemType" to itemType.name
                    )
                )

            findNavController().popBackStack()
        }

        binding.saveAmountText.setOnClickListener {
            if (itemType == ItemType.QTY)
                parentFragmentManager.setFragmentResult(
                    "handleAmountAndUnitUpdate",
                    bundleOf(
                        "amount" to updatedAmount,
                        "amountUnit" to updatedAmountUnit,
                        "itemType" to itemType.name
                    )
                )
            else if (itemType == ItemType.DEFECTIVE)
                parentFragmentManager.setFragmentResult(
                    "handleDefectiveAmountAndUnitUpdate",
                    bundleOf(
                        "amount" to updatedAmount,
                        "amountUnit" to updatedAmountUnit,
                        "itemType" to itemType.name
                    )
                )

            findNavController().popBackStack()
        }
    }

    private fun initViews(itemType: ItemType) {

        var headerTitle = getString(R.string.amount_of_items)
        var textHint = getString(R.string.amount_of_items)
        val unitTypeButtonText = getString(R.string.change_unit_type)

        when (itemType) {
            ItemType.QTY -> {
                headerTitle = getString(R.string.amount_of_items)
                textHint = getString(R.string.amount_of_items)
                binding.articleCodeTextInput.isEndIconVisible = false
                binding.amountItemText.updateWithNumberTextWatcher { updatedNewAmountItem ->
                    updatedAmount = updatedNewAmountItem
                }


            }

            ItemType.DEFECTIVE -> {
                headerTitle = getString(R.string.defective_items)
                textHint = getString(R.string.defective_items)
                binding.articleCodeTextInput.isEndIconVisible = true
                binding.amountItemText.updateWithNumberTextWatcher { updatedNewAmountItem ->
                    updatedAmount = updatedNewAmountItem
                }

                binding.articleCodeTextInput.setEndIconOnClickListener {
                    showDefectiveArticleBottomSheet()
                }
            }
        }


        with(binding) {
            deliveryHeader.headerTitle.text = headerTitle
            articleCodeTextInput.hint = textHint
            amountItemText.setText(updatedAmount)
            amountItemText.text?.length?.let { amountItemText.setSelection(it) }
            changeUnitTypeText.text = unitTypeButtonText
            var value = binding.amountItemText.text.toString().trim()
            if(value.isEmpty()) value = "0"
            // Add focus change listener
            binding.amountItemText.onFocusChangeListener =
                View.OnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        // Clear the text when EditText is focused
                        if (value.toFloat() == 0f)
                            binding.amountItemText.setText("")
                        else
                            binding.amountItemText.setText("${(value.toFloat())}")

                    } else {
                        // Restore the text when focus is lost
                        binding.amountItemText.setText(value)

                    }
                }

        }
    }

    private fun showDefectiveArticleBottomSheet() {
        val bottomSheet = DefectiveItemInfoBottomSheetFragment()
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }


    private fun handleBackButton() {
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showUnitMeterTypeBottomSheet() {
        val bottomSheet =
            UnitMeterTypeBottomSheetFragment.newInstance(
                itemType = itemType,
                selectedUnit = updatedAmountUnit
            ).apply {
                    listener = this@AmountItemFragment
                }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUnitMeterTypeSelected(articleUnit: ArticleUnit) {
        updatedAmountUnit = articleUnit.unitCode
    }
}