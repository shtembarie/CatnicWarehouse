package com.example.catnicwarehouse.CorrectingStock.articles.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionBinding
import com.example.catnicwarehouse.movement.articles.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.movement.articles.presentation.adapter.ArticleSelectionAdapterInteraction
import com.example.catnicwarehouse.movement.articles.presentation.fragment.ArticleSelectionFragmentDirections
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

class ArticleSelectionFragment : BaseFragment(), ArticleSelectionAdapterInteraction {
    private var _binding: FragmentArticleSelectionBinding? = null
    private val binding get() = _binding!!
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        articleAdapter.submitList(movementSharedViewModel.articlesListToSelectFrom)
    }
    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            movementSharedViewModel.scannedStockyard?.name ?: getString(R.string.articles)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setUpAdapter() {
        articleAdapter =
            ArticleSelectionAdapter(
                interaction = this,
                requireContext(),
                movementSharedViewModel)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = articleAdapter

    }

    override fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel) {
        // In case the article already exists in movement items list, disable its selection
        movementSharedViewModel.selectedArticle = data
        val action =
            com.example.catnicwarehouse.CorrectingStock.articles.presentation.fragments.ArticleSelectionFragmentDirections.actionArticleSelectionFragment4ToMatchFoundCorrectiveStockFragment()
        findNavController().navigate(action)
    }

}
