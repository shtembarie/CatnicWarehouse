package com.example.catnicwarehouse.scan.presentation.bottomSheetFragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionListBottomSheetBinding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.scan.presentation.adapter.ArticlesSelectionAdapter
import com.example.catnicwarehouse.scan.presentation.enums.ScanType

import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.*
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSelectionListBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentArticleSelectionListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()

    private lateinit var articlesAdapter: ArticlesSelectionAdapter
    private var listener: OnArticleSelectedListener? = null

    private lateinit var moduleType: ModuleType

    companion object {
        private const val ARTICLE_LIST = "article_list"
        private const val ARG_MODULE_TYPE = "module_type"

        fun newInstance(
            articleList: List<ArticlesForDeliveryResponseDTO>?,
            moduleType: ModuleType
        ): ArticleSelectionListBottomSheet {
            return ArticleSelectionListBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARTICLE_LIST, ArrayList(articleList ?: emptyList()))
                    putParcelable(ARG_MODULE_TYPE, moduleType)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentArticleSelectionListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handelHeaderSection()
        val articleList =
            arguments?.getParcelableArrayList<ArticlesForDeliveryResponseDTO>(ARTICLE_LIST)
                ?: emptyList()
        moduleType = arguments?.getParcelable(ARG_MODULE_TYPE)
            ?: throw IllegalArgumentException("ModuleType is missing")

        // Initialize adapter
        articlesAdapter = ArticlesSelectionAdapter(requireContext()) { article ->
            listener?.onArticleSelected(article)
            when (moduleType) {
                MOVEMENTS -> {
                    movementSharedViewModel.scannedArticle = article
                }
                PACKING_1 -> {
                    packingSharedViewModel.scannedArticle = article
                }
                DEFECTIVE_ITEMS -> {
                    defectiveArticleSharedViewModel.scannedArticle = article
                }
                CHECKS -> {
                    checksSharedViewModel.scannedArticle = article
                }

                INCOMING -> {}
                INVENTORY -> {
                    inventorySharedViewModel.scannedArticle = article
                }
                PACKING_2 -> {}
                CORRECTIVE_STOCK -> {}
            }
            parentFragmentManager.setFragmentResult(
                "handleSuccessArticleScan",
                bundleOf(
                    "scanType" to ScanType.ARTICLE,
                    "scannedArtikelId" to article.articleId
                )
            )
            dismiss()
        }

        // Set up RecyclerView
        binding.articlesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articlesAdapter
        }

        // Provide the data to the adapter
        articlesAdapter.submitList(articleList)
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.select_articles)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            dismiss()
        }
    }

    // Force full height if desired
    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            val bottomSheet =
                d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = (resources.displayMetrics.heightPixels * 1).toInt()
                behavior.skipCollapsed = true
                isCancelable = false
            }
        }
    }

    interface OnArticleSelectedListener {
        fun onArticleSelected(article: ArticlesForDeliveryResponseDTO)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent is OnArticleSelectedListener) {
            listener = parent
        } else if (context is OnArticleSelectedListener) {
            listener = context
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
