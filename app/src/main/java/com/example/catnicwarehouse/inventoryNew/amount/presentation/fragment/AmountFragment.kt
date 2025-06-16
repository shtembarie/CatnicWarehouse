package com.example.catnicwarehouse.inventoryNew.amount.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.bottomSheet.UnitTypeBottomSheetInventory
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAmount4Binding
import com.example.catnicwarehouse.incoming.amountItem.presentation.bottomSheetFragment.UnitMeterTypeBottomSheetFragment
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.shared.networking.network.article.ArticleUnit
import com.example.shared.repository.inventory.model.SetInventoryItems
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmountFragment : BaseFragment(),
    com.example.catnicwarehouse.inventoryNew.amount.presentation.bottomsheet.UnitTypeBottomSheetInventory.UnitMeterTypeListener {

    private var _binding: FragmentAmount4Binding? = null
    private val binding get() = _binding!!

    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAmount4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        populateUIWithData()
        handleUnitTypeClickAction()
        handleSaveButtonAction()
    }

    private fun populateUIWithData() {
        binding.amountItemText.setText(
            (inventorySharedViewModel.updatedAmount
                ?: inventorySharedViewModel.selectedInventoryItem?.actualStock
                ?: inventorySharedViewModel.selectedArticle?.amount
                ?: 0).toString()
        )
    }

    private fun handleSaveButtonAction() {
        binding.saveAmountText.setOnClickListener {
            val raw = binding.amountItemText.text.toString().trim()
            val normalized = when {
                raw.isEmpty() -> "0"
                raw.startsWith('.') -> "0$raw"
                else -> raw
            }
            inventorySharedViewModel.updatedAmount = normalized.toIntOrNull() ?: 0
            findNavController().popBackStack()
        }
    }


    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.counted_in_stock)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleUnitTypeClickAction() {
        binding.changeUnitTypeLayout.setOnClickListener {
            showUnitMeterTypeBottomSheet()
        }
    }

    private fun showUnitMeterTypeBottomSheet() {
        val bottomSheet =
            com.example.catnicwarehouse.inventoryNew.amount.presentation.bottomsheet.UnitTypeBottomSheetInventory.newInstance(
                selectedUnit = inventorySharedViewModel.updatedUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.actualUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.targetUnitCode
                    ?: inventorySharedViewModel.selectedArticle?.unitCode
            ).apply {
                listener = this@AmountFragment
            }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUnitMeterTypeSelected(unitMeterType: ArticleUnit) {
        inventorySharedViewModel.updatedUnitCode = unitMeterType.unitCode
    }

}