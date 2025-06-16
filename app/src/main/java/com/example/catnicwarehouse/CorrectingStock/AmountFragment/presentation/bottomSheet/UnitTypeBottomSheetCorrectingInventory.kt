package com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.fragment.ItemAmountFragment
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsEvent
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsViewState
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.viewModel.UnitTypeViewModel
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.adapter.UnitTypeAdapter
import com.example.catnicwarehouse.databinding.UnitMeterBottomSheetBinding
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.shared.repository.correctingStock.model.GetArticleUnitsParamsByUIModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_correcting_stock.*
import kotlinx.android.synthetic.main.fragment_movement_summary.*
import kotlinx.android.synthetic.main.fragment_movement_summary.view.*
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


/**
 * Created by Enoklit on 18.11.2024.
 */
@AndroidEntryPoint
class UnitTypeBottomSheetCorrectingInventory : BottomSheetDialogFragment() {
    private var _binding: UnitMeterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var unitTypeAdapter: UnitTypeAdapter
    private var onUnitTypeSelected: ((String) -> Unit)? = null
    private val viewModel: UnitTypeViewModel by activityViewModels()
    private var articleUnits: List<GetArticleUnitsParamsByUIModel>? = null
    private val corrStockSharedViewModel: CorrStockSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UnitMeterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUnitTypes()
        fetchUnitTypes()
    }

    private fun fetchUnitTypes() {
        val articleId = corrStockSharedViewModel.articleId
        viewModel.onEvent(GetArticleUnitsEvent.Loading(articleId.toString()))
    }

    private fun observeUnitTypes() {
        viewModel.getUnitCode.onEach { state ->
            when (state) {
                is GetArticleUnitsViewState.ArticleUnitId -> {
                    articleUnits = state.articleUnits?.map { unit ->
                        GetArticleUnitsParamsByUIModel(
                            unitCode = unit.unitCode,
                            gtin = unit.gtin,
                            labelText = unit.labelText,
                            netWeight = unit.netWeight,
                            grossWeight = unit.grossWeight,
                            baseAmountPerUnit = unit.baseAmountPerUnit,
                            amountPerBase = unit.amountPerBase,
                            isBaseCode = unit.isBaseCode
                        )
                    }
                    setUpRecyclerView(articleUnits)
                }
                GetArticleUnitsViewState.Empty -> {}
                is GetArticleUnitsViewState.Error -> {}
                GetArticleUnitsViewState.Loading -> {}
                GetArticleUnitsViewState.Reset -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun setUpRecyclerView(units: List<GetArticleUnitsParamsByUIModel>?) {
        val initialSelectedUnitCode = corrStockSharedViewModel.unitCode
        val unitTypes = units?.map { it.unitCode } ?: emptyList() // Extract `unitCode` from API response
        unitTypeAdapter = UnitTypeAdapter(
            unitTypes,
            onUnitTypeClick = { unitType ->
                // Invoke callback for additional actions
                onUnitTypeSelected?.invoke(unitType)
                // Update in shared ViewModel
                corrStockSharedViewModel.unitCode = unitType
                dismiss()
            },
            selectedUnitCode = initialSelectedUnitCode
        )
        binding.rView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unitTypeAdapter
        }
    }

    fun setOnUnitTypeSelectedListener(listener: (String) -> Unit) {
        onUnitTypeSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
