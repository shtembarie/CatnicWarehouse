package com.example.catnicwarehouse.scan.presentation.bottomSheetFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionListBottomSheetBinding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter.ArticlesBasedOnStockyardEntriesSelectionAdapter
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.*
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSelectionListBasedOnStockyardBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentArticleSelectionListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()

    private lateinit var entriesAdapter: ArticlesBasedOnStockyardEntriesSelectionAdapter
    private var listener: OnEntrySelectedListener? = null

    companion object {
        private const val ENTRY_LIST = "entry_list"
        private const val ARG_MODULE_TYPE = "module_type"

        fun newInstance(
            entryList: List<WarehouseStockyardInventoryEntriesResponseModel>?,
            moduleType: ModuleType
        ): ArticleSelectionListBasedOnStockyardBottomSheet {
            return ArticleSelectionListBasedOnStockyardBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        ENTRY_LIST,
                        ArrayList(entryList ?: emptyList())
                    )
                    putParcelable(ARG_MODULE_TYPE, moduleType)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleSelectionListBottomSheetBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleHeaderSection()

        val entryList = arguments?.getParcelableArrayList<WarehouseStockyardInventoryEntriesResponseModel>(ENTRY_LIST) ?: emptyList()
        val moduleType = arguments
            ?.getParcelable<ModuleType>(ARG_MODULE_TYPE)
            ?: throw IllegalArgumentException("ModuleType is missing")

        entriesAdapter = ArticlesBasedOnStockyardEntriesSelectionAdapter(requireContext()) { entry ->
            listener?.onEntrySelected(entry)
            when (moduleType) {
                INVENTORY           -> inventorySharedViewModel.selectedArticle = entry
                else                -> { }
            }
            parentFragmentManager.setFragmentResult(
                "handleSuccessArticleScan",
                bundleOf(
                    "scanType" to ScanType.ARTICLE,
                    "scannedArtikelId" to entry.articleId
                )
            )
            dismiss()
        }

        binding.articlesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entriesAdapter
        }
        entriesAdapter.submitList(entryList)
    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.select_articles)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = (resources.displayMetrics.heightPixels * 1).toInt()
                behavior.skipCollapsed = true
                isCancelable = false
            }
    }

    interface OnEntrySelectedListener {
        fun onEntrySelected(entry: WarehouseStockyardInventoryEntriesResponseModel)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is OnEntrySelectedListener -> parentFragment as OnEntrySelectedListener
            context is OnEntrySelectedListener         -> context
            else                                        -> null
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}