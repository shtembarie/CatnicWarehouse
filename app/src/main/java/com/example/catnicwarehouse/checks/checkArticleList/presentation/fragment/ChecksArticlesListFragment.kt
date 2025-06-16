package com.example.catnicwarehouse.checks.checkArticleList.presentation.fragment

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
import com.example.catnicwarehouse.checks.checkArticleList.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.checks.checkArticleList.presentation.adapter.ArticleSelectionAdapterInteraction
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.databinding.FragmentChecksBinding
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChecksArticlesListFragment : BaseFragment(), ArticleSelectionAdapterInteraction {

    private var _binding: FragmentChecksBinding? = null
    private val binding get() = _binding!!


    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleSelectionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        articleAdapter.submitList(checksSharedViewModel.articlesListToSelectFrom)
    }


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            checksSharedViewModel.articlesListToSelectFrom?.get(0)?.articleId
                ?: getString(R.string.stockyard)
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
                checksSharedViewModel,
                isForArticlesList = true
            )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = articleAdapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel) {

    }


}