package com.example.catnicwarehouse.inventoryNew.articles.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionBinding
import com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter.ArticleSelectionAdapterInteraction
import com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter.ArticlesBasedOnStockyardEntriesSelectionAdapter
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSelectionFragment : BaseFragment() {

    private var _binding: FragmentArticleSelectionBinding? = null
    private val binding get() = _binding!!
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticlesBasedOnStockyardEntriesSelectionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        articleAdapter.submitList(inventorySharedViewModel.articlesListToSelectFrom)
    }


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            inventorySharedViewModel.scannedStockyard?.name ?: getString(R.string.stockyard)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpAdapter() {
        articleAdapter =
            ArticlesBasedOnStockyardEntriesSelectionAdapter(
                context = requireContext(),
            ) { selectedWarehouseStockyardInventoryEntriesResponseModel->
                inventorySharedViewModel.selectedInventoryItem = null
                inventorySharedViewModel.selectedArticle = selectedWarehouseStockyardInventoryEntriesResponseModel
                val action =
                    ArticleSelectionFragmentDirections.actionArticleSelectionFragment5ToMatchFragment()
                findNavController().navigate(action)
            }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = articleAdapter

    }


}

