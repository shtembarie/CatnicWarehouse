package com.example.catnicwarehouse.packing.addPackingItems.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAddPackingItemsBinding
import com.example.catnicwarehouse.packing.addPackingItems.presentation.adapter.PackingItemsAdapter
import com.example.catnicwarehouse.packing.addPackingItems.presentation.adapter.PackingItemsAdapterInteraction
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsEvent
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsViewState
import com.example.catnicwarehouse.packing.addPackingItems.presentation.viewModel.AddPackingItemsViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.WarehouseStockYardPicking
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AddPackingItemsFragment : BaseFragment(), PackingItemsAdapterInteraction {

    private var _binding: FragmentAddPackingItemsBinding? = null
    private val binding get() = _binding!!

    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val viewModel: AddPackingItemsViewModel by viewModels()

    private lateinit var adapter: PackingItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPackingItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
        handleDoneButtonAction()
        observeAddPackingItemsEvents()
        setupAdapter()
        (packingSharedViewModel.selectedAssignedPackingListItem?.id
            ?: packingSharedViewModel.selectedSearchedPackingListId)?.let {
            viewModel.onEvent(AddPackingItemsEvent.GetItemsForPacking(it))
        }
    }

    private fun setupAdapter() {
        adapter = PackingItemsAdapter(this, requireContext())
        binding.packingList.layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.adapter = adapter
    }

    private fun observeAddPackingItemsEvents() {
        viewModel.addPackingItemsFlow.onEach { state ->
            when (state) {
                AddPackingItemsViewState.Empty -> progressBarManager.dismiss()
                is AddPackingItemsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is AddPackingItemsViewState.GetItemsForPackingResponse -> {
                    progressBarManager.dismiss()

                    val filteredList = state.itemsForPackingItems?.let {
                        packingSharedViewModel.selectedArticle?.articleId?.let { it1 ->
                            filterPackingItemsByArticleId(
                                it,
                                it1
                            )
                        }
                    }

                    if (filteredList.isNullOrEmpty()) {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.packingList.visibility = View.GONE
                    } else {
                        binding.emptyLayout.visibility = View.GONE
                        binding.packingList.visibility = View.VISIBLE
                    }

                    packingSharedViewModel.itemsForPacking = state.itemsForPackingItems
                    handleHeaderSection()
                    filteredList?.let {
                        adapter.submitList(
                            it
                        )
                    }
                }

                AddPackingItemsViewState.Loading -> progressBarManager.show()
                AddPackingItemsViewState.Reset -> progressBarManager.dismiss()
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleDoneButtonAction() {
        binding.addItemButton.setOnClickListener {
            navigateToAvailableStockyardsFragment()
        }
        binding.addItems.setOnClickListener {
            navigateToAvailableStockyardsFragment()
        }
    }

    private fun navigateToAvailableStockyardsFragment() {
        val action =
            AddPackingItemsFragmentDirections.actionAddPackingItemsFragmentToAvailableStockyardsFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAmountFragmentToUpdate() {
        val action =
            AddPackingItemsFragmentDirections.actionAddPackingItemsFragmentToAmountFragment2()
        action.isUpdatingAmount = true
        findNavController().navigate(action)
    }


    private fun handleHeaderSection() {

        val pickUpAmount =
            "${
                packingSharedViewModel.itemsForPacking?.let {
                    packingSharedViewModel.selectedArticle?.articleId?.let { it1 ->
                        it.filter { it.articleId == it1 }
                    }
                }?.sumOf { s -> s.packedAmount }
            }"

        val amountAvailable =
            "${
                (packingSharedViewModel.selectedPackingItemToPack?.amount?.toInt() ?: 0)
            }"

        binding.deliveryHeader.headerTitle.text =
            getString(
                R.string.packed_items,
                pickUpAmount,
                amountAvailable
            ).colorSubstringFromCharacter(
                '/',
                Color.LTGRAY
            )

    }

    private fun filterPackingItemsByArticleId(
        packingItems: List<GetItemsForPackingResponseModelItem>,
        articleId: String
    ): List<WarehouseStockYardPicking> {
        // Filter items by articleId and flatten the list of pickings
        return packingItems
            .filter { it.articleId == articleId }
            .flatMap { it.warehouseStockYardPickings }
    }

    override fun onViewClicked(data: WarehouseStockYardPicking) {
        packingSharedViewModel.selectedItemForPacking = data
        navigateToAmountFragmentToUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(AddPackingItemsEvent.Reset)
    }


}