package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentStockyardSelectionBinding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.DefectiveArticlesAdapter
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.DefectiveItemsAdapterInteraction
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.StockyardSelectedInteraction
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.StockyardSelectionAdapter
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockyardSelectionFragment : BaseFragment(), StockyardSelectedInteraction {

    private var _binding: FragmentStockyardSelectionBinding? = null
    private val binding get() = _binding!!
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private lateinit var selectionStockyardsAdapter: StockyardSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockyardSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        setUpAdapter()
        selectionStockyardsAdapter.submitList(defectiveArticleSharedViewModel.warehouseStockyardInventoryEntry)
    }
    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.stockyards)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpAdapter() {
        selectionStockyardsAdapter =
            StockyardSelectionAdapter(
                interaction = this,
                requireContext(),
                )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = selectionStockyardsAdapter

    }
    override fun onStockyardClicked(stockyard: WarehouseStockyardInventoryEntriesResponseModel) {
        defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry = stockyard
        val action =
            StockyardSelectionFragmentDirections.actionStockyardSelectionFragmentToMatchFoundDefectiveItemsFragment()
        findNavController().navigate(action)
    }
}