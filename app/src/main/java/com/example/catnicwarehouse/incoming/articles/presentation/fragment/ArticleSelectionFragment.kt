package com.example.catnicwarehouse.incoming.articles.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionBinding
import com.example.catnicwarehouse.incoming.articles.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.incoming.articles.presentation.adapter.ArticleSelectionAdapterInteraction
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.viewModel.UnloadingStockyardsViewModel
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSelectionFragment : BaseFragment(), ArticleSelectionAdapterInteraction {

    private var _binding: FragmentArticleSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UnloadingStockyardsViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private lateinit var articleAdapter: ArticleSelectionAdapter


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
        articleAdapter.submitList(sharedViewModel.getArticleItemModelListFromSearchedArticle())
    }


    @SuppressLint("SetTextI18n")
    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.select_articles)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
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
                sharedViewModel)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = articleAdapter

    }

    override fun onViewClicked(data: ArticlesForDeliveryResponseDTO) {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedArticleItemModel(
                SharedEvent.mapArticleItemForDeliveryToArticleItemUI(
                    data
                )
            ),
            SharedEvent.UpdateSelectedQtyUnit(
                data.unitCode?:""
            ),
            SharedEvent.UpdateSelectedDefectiveUnit(
                data.unitCode?:""
            ),
            SharedEvent.UpdateSelectedQty(data.quantityInPurchaseOrders.toString())
        )


        val action = ArticleSelectionFragmentDirections.actionArticleSelectionFragment3ToUnloadingStockyardFragment()
        findNavController().navigate(action)
    }
}

