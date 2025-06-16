package com.example.catnicwarehouse.inventoryNew.amount.presentation.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsEvent
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses.GetArticleUnitsViewState
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.viewModel.UnitTypeViewModel
import com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.adapter.UnitTypeAdapter
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.UnitMeterBottomSheetBinding
import com.example.catnicwarehouse.incoming.amountItem.presentation.adapter.UnitMeterTypeAdapter
import com.example.catnicwarehouse.incoming.amountItem.presentation.bottomSheetFragment.UnitMeterTypeBottomSheetFragment.UnitMeterTypeListener
import com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses.AmountItemEvent
import com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses.AmountItemViewState
import com.example.catnicwarehouse.inventoryNew.amount.presentation.viewModel.AmountViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.shared.presentation.enums.ItemType
import com.example.shared.networking.network.article.ArticleUnit
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnitTypeBottomSheetInventory : BottomSheetDialogFragment() {

    private var _binding: UnitMeterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val viewModel: AmountViewModel by viewModels()
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }

    interface UnitMeterTypeListener {
        fun onUnitMeterTypeSelected(unitMeterType: ArticleUnit)
    }
    var listener: UnitMeterTypeListener? = null
    private var selectedUnit:String? = null

    companion object {
        private const val ITEM_TYPE = "item_type"
        private const val SELECTED_UNIT = "selected_unit"

        fun newInstance(
            selectedUnit: String?
        ): UnitTypeBottomSheetInventory {
            return UnitTypeBottomSheetInventory().apply {
                arguments = Bundle().apply {
                    putSerializable(SELECTED_UNIT, selectedUnit)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UnitMeterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedUnit = arguments?.getString(SELECTED_UNIT)
        viewModel.onEvent(
            AmountItemEvent.GetArticleUnits(
                inventorySharedViewModel.selectedInventoryItem?.articleId
                    ?:inventorySharedViewModel.selectedArticle?.articleId
                    ?:""
            )
        )
        observeAmountItemEvents()
    }

    private fun observeAmountItemEvents() {
        viewModel.amountItemFlow.onEach { state ->
            when (state) {
                is AmountItemViewState.ArticleUnits -> {
                    progressBarManager.dismiss()
                    val articleUnits = state.articleUnits
                    initAdapter(
                        articleUnits = articleUnits,
                        selectedQuantityUnit = selectedUnit
                    )
                }

                AmountItemViewState.Empty -> progressBarManager.dismiss()
                is AmountItemViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                AmountItemViewState.Loading -> progressBarManager.show()
                AmountItemViewState.Reset -> progressBarManager.dismiss()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initAdapter(articleUnits: List<ArticleUnit>?, selectedQuantityUnit: String?) {
        val adapter =
            UnitMeterTypeAdapter(
                unitMeterTypes = articleUnits,
                selectedUnitType = selectedQuantityUnit
            ) { unitMeterType ->
                listener?.onUnitMeterTypeSelected(unitMeterType)
                dismiss()
            }
        binding.rView.adapter = adapter
        binding.rView.layoutManager = LinearLayoutManager(context)
    }

    fun showErrorBanner(message: String, displayDuration: Long = 2000) {

        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.red)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}