package com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticleSelectionBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.ArticleSelectionAdapterInteraction
import com.example.catnicwarehouse.packing.packingItem.presentation.fragment.PackingItemsFragmentArgs
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSelectionFragment : BaseFragment(), ArticleSelectionAdapterInteraction {

    private var _binding: FragmentArticleSelectionBinding? = null
    private val binding get() = _binding!!
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleSelectionAdapter
    private val args: PackingItemsFragmentArgs by navArgs()


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
        articleAdapter.submitList(packingSharedViewModel.articlesListToSelectFrom)
    }


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            args.packingId
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpAdapter() {
        articleAdapter =
            ArticleSelectionAdapter(interaction = this, requireContext(), packingSharedViewModel)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = articleAdapter

    }

    override fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel) {
        packingSharedViewModel.selectedArticle = data
        packingSharedViewModel.selectedPackingItemToPack =
            packingSharedViewModel.packingItems?.filter { s->s.articleId == data.articleId }
                ?.get(0)
        navigateToMatchFoundFragment()
    }

    private fun navigateToMatchFoundFragment(){
        val action =
            ArticleSelectionFragmentDirections.actionArticleSelectionFragment2ToMatchFoundFragment2()
        findNavController().navigate(action)
    }
}

