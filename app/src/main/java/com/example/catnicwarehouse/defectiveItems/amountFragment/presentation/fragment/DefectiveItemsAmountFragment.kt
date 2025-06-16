package com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.fragment

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.bottomSheet.UnitTypeBottomSheetCorrectingInventory
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDefectiveItemsAmountBinding
import com.example.catnicwarehouse.databinding.FragmentDefectiveItemsBinding
import com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses.UpdateAmountEvent
import com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses.UpdateAmountViewState
import com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.viewModel.UpdateAmountViewModel
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.shared.repository.defectiveArticles.SetAmount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DefectiveItemsAmountFragment : BaseFragment() {
    private var _binding: FragmentDefectiveItemsAmountBinding? = null
    private val binding get() = _binding!!
    private val updateAmountViewModel: UpdateAmountViewModel by viewModels()
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDefectiveItemsAmountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        setUpSaveButton()
        getParams()
  }
    private fun handleHeaderSection(){
        binding.deliveryHeader.headerTitle.text = getString(R.string.counted_in_stock)
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.deliveryHeader.rightIconButton.visibility = View.GONE
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
            defectiveArticleSharedViewModel.saveUpdatedAmount(itemAmount)
        }
        findNavController().popBackStack()
    }
    private fun getParams() {
        val initialAmount = (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.defectiveAmount ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.amount).toString()
        val updatedAmount = defectiveArticleSharedViewModel.updadeAmount

        val amountText = updatedAmount?.toString() ?: initialAmount?.toString() ?: ""
        binding.amountItemText.text = Editable.Factory.getInstance().newEditable(amountText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}