package com.example.catnicwarehouse.incoming.amountItem.presentation.bottomSheetFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentUnitMeterTypeBottomSheetBinding
import com.example.catnicwarehouse.incoming.amountItem.presentation.adapter.UnitMeterTypeAdapter
import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemEvent
import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemViewState
import com.example.catnicwarehouse.incoming.amountItem.presentation.viewModel.AmountItemViewModel
import com.example.catnicwarehouse.shared.presentation.enums.ItemType
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.article.ArticleUnit
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UnitMeterTypeBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ITEM_TYPE = "item_type"
        private const val SELECTED_UNIT = "selected_unit"

        fun newInstance(
            itemType: ItemType,
            selectedUnit: String?
        ): UnitMeterTypeBottomSheetFragment {
            return UnitMeterTypeBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ITEM_TYPE, itemType)
                    putSerializable(SELECTED_UNIT, selectedUnit)
                }
            }
        }
    }

    private var _binding: FragmentUnitMeterTypeBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val viewModel: AmountItemViewModel by viewModels()
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }
    private var articleUnits: List<ArticleUnit>? = null
    private lateinit var itemType: ItemType
    private var selectedUnit: String? = null

    interface UnitMeterTypeListener {
        fun onUnitMeterTypeSelected(unitMeterType: ArticleUnit)
    }

    var listener: UnitMeterTypeListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemType = it.getParcelable(ITEM_TYPE)
                ?: throw IllegalArgumentException("Item type is missing")
            selectedUnit = it.getString(SELECTED_UNIT)
                ?: throw IllegalArgumentException("Selected unit is missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitMeterTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEvent(
            AmountItemEvent.GetArticleUnits(
                sharedViewModel.getSelectedArticleItemModel()?.articleId ?: ""
            )
        )
        observeAmountItemEvents()
    }

    private fun observeAmountItemEvents() {
        viewModel.amountItemFlow.onEach { state ->
            when (state) {
                is AmountItemViewState.ArticleUnits -> {
                    progressBarManager.dismiss()
                    articleUnits = state.articleUnits
                    if (itemType == ItemType.QTY) {
                        (selectedUnit ?: sharedViewModel.getSelectedQuantityUnit())?.let {
                            initAdapter(
                                selectedQuantityUnit = it
                            )
                        }
                    } else if (itemType == ItemType.DEFECTIVE) {
                        (selectedUnit ?: sharedViewModel.getSelectedDefectiveUnit())?.let {
                            initAdapter(
                                selectedQuantityUnit = it
                            )
                        }
                    }
                    observeSharedEvent()
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

    private fun observeSharedEvent() {
//        sharedVM.deliverySharedFlow.onEach { state ->
//            when (state) {
//                DeliverySharedViewState.Empty -> progressBarManager.dismiss()
//                is DeliverySharedViewState.Error -> progressBarManager.dismiss()
//                DeliverySharedViewState.Loading -> progressBarManager.show()
//                is DeliverySharedViewState.UpdatedValue -> {
//                    progressBarManager.dismiss()
//                    when (state.deliveryLocalModel.itemType) {
//                        QTY -> initAdapter(state.deliveryLocalModel.selectedQuantityUnit)
//                        DEFECTIVE -> initAdapter(state.deliveryLocalModel.localDefectiveUnit)
//                    }
//
//                }
//
//                else -> {
//                    progressBarManager.dismiss()
//                }
//            }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun initAdapter(selectedQuantityUnit: String) {
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
