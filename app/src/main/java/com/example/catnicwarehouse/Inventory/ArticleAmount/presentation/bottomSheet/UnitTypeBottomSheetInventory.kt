package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsEvent
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsViewState
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.viewModel.UnitTypeViewModel
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.adapter.UnitTypeAdapter
import com.example.catnicwarehouse.databinding.UnitMeterBottomSheetBinding
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class UnitTypeBottomSheetInventory : BottomSheetDialogFragment() {

    private var _binding: UnitMeterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var unitTypeAdapter: UnitTypeAdapter
    private var onUnitTypeSelected: ((String) -> Unit)? = null
    private val viewModel: UnitTypeViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()

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
        val articleId = inventorySharedViewModel.articleId
        if (!articleId.isNullOrEmpty()) {
            viewModel.onEvent(GetArticleUnitsEvent.Loading(articleId))
        } else {
            Toast.makeText(requireContext(), "Article ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUnitTypes() {
        viewModel.getUnitCode.onEach { state ->
            when (state) {
                is GetArticleUnitsViewState.ArticleUnitId -> {
                    val units = state.articleUnits?.map { it.unitCode } ?: emptyList()
                    setUpRecyclerView(units)
                }
                GetArticleUnitsViewState.Loading -> {}
                is GetArticleUnitsViewState.Error -> {}
                else -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setUpRecyclerView(unitTypes: List<String>) {
        val itemId = inventorySharedViewModel.itemId
        val actualUnitCode = itemId?.let { inventorySharedViewModel.updatedActualUnitCode[it] }
            ?: inventorySharedViewModel.unitCodeActual ?:inventorySharedViewModel.inventoryItem?.actualUnitCode

        unitTypeAdapter = UnitTypeAdapter(
            unitTypes,
            { unitType ->
                onUnitTypeSelected?.invoke(unitType)
                if (itemId != null) {
                    inventorySharedViewModel.saveUpdatedActualUnitCode(itemId, unitType)
                }
                dismiss()
            },
            actualUnitCode.toString()
        )

        binding.rView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unitTypeAdapter
        }
    }

    fun updateSelectedUnitType(unitType: String) {
        unitTypeAdapter.updateSelectedUnitType(unitType)
    }

    fun setOnUnitTypeSelectedListener(listener: (String) -> Unit) {
        onUnitTypeSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

