package com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.bottomSheet.UnitTypeBottomSheetCorrectingInventory
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsViewState
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.viewModel.UnitTypeViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentItemAmountBinding
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.shared.repository.correctingStock.model.GetArticleUnitsParams
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ItemAmountFragment : BaseFragment() {
    private var _binding: FragmentItemAmountBinding? = null
    private val binding get() = _binding!!
    private val corrStockSharedViewModel: CorrStockSharedViewModel by activityViewModels()
    private val viewModel: UnitTypeViewModel by activityViewModels()
    private var articleUnits: List<GetArticleUnitsParams>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemAmountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        setUpSaveButton()
        setUpChangeUnitTypeButton()
        getParams()
    }

    private fun handleHeaderSection(){
        binding.deliveryHeader.headerTitle.text = getString(R.string.counted_in_stock)
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpSaveButton(){
        updateSaveButtonState(binding.amountItemText.text?.isNotEmpty() ?: false)
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
        val itemAmountText = binding.amountItemText.text.toString()
        val itemAmount = itemAmountText.toIntOrNull()
        if (itemAmount != null) {
            corrStockSharedViewModel.saveUpdatedAmount(itemAmount)
        }
        findNavController().popBackStack()
    }
    private fun getParams() {
        val initialAmount = corrStockSharedViewModel.amount
        val updatedAmount = corrStockSharedViewModel.updadeAmount

        val amountText = updatedAmount?.toString() ?: initialAmount?.toString()
        binding.amountItemText.text = Editable.Factory.getInstance().newEditable(amountText)

    }
    private fun updateSaveButtonState(isEnabled: Boolean) {
        val drawableRes = if (isEnabled) {
            R.drawable.orange_rounded_button
        } else {
            R.drawable.grey_rounded_button
        }
        binding.saveAmountButton.setBackgroundResource(drawableRes)
        binding.saveButton.setBackgroundResource(drawableRes)
    }
    private fun setUpChangeUnitTypeButton() {
        binding.changeUnitTypeLayout.setOnClickListener {
            val unitTypeBottomSheet = UnitTypeBottomSheetCorrectingInventory()
            unitTypeBottomSheet.setOnUnitTypeSelectedListener { unitType ->
                Toast.makeText(requireContext(), "Selected: $unitType", Toast.LENGTH_SHORT).show()
                corrStockSharedViewModel.unitCode = unitType
            }
            unitTypeBottomSheet.show(parentFragmentManager, unitTypeBottomSheet.tag)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}