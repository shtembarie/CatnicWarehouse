package com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentFinalisePackingBinding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.viewModel.FinalisePackingViewModel
import com.example.catnicwarehouse.packing.packingItem.presentation.adapter.PackingItemsAdapter
import com.example.catnicwarehouse.packing.packingItem.presentation.adapter.PackingItemsAdapterInteraction
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.shared.networking.network.packing.model.packingItem.PackingItem
import com.example.shared.networking.network.packing.model.packingList.ConnectedPackingList
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FinalisePackingFragment : BaseFragment(), PackingItemsAdapterInteraction{

    private var _binding: FragmentFinalisePackingBinding? = null
    private val binding get() = _binding!!

    private val args: FinalisePackingFragmentArgs by navArgs()
    private var packingId: String? = null
    private lateinit var packingItemsAdapter: PackingItemsAdapter
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val viewModel: FinalisePackingViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFinalisePackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        packingId = args.packingId
        handleHeaderSection()
        setUpAdapter()
        observeFinalisePackingEvents()
        handleRelatedPackingListButtonAction()
        binding.emptyLayout.visibility = View.GONE
        binding.packingList.visibility = View.VISIBLE
        viewModel.onEvent(FinalisePackingEvent.GetPackingItems(id = packingId))

    }


    private fun handleRelatedPackingListButtonAction() {
        binding.relatedPackingListLayout.setOnClickListener {
            val action =
                FinaliseParentFragmentDirections.actionFinalisePackingFragmentToPackingListFragment()
            action.isForRelatedPackingList = true
            findNavController().navigate(action)
        }
    }




    @SuppressLint("SetTextI18n")
    private fun handleHeaderSection() {
        val isVisible =
            packingSharedViewModel.selectedPackingListItem?.connectedPackingLists.isNullOrEmpty()
                .not()
        binding.relatedPackingListLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.relatedPackingListTextView.text = getString(
            R.string.related_packing_list,
            getConnectedPackingListIds(packingSharedViewModel.selectedPackingListItem?.connectedPackingLists)
        )

    }

    private fun getConnectedPackingListIds(connectedPackingLists: List<ConnectedPackingList>?): String {
        return connectedPackingLists?.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = ", ") { it.id }
            ?: ""
    }


    private fun setUpAdapter() {
        packingItemsAdapter =
            PackingItemsAdapter(interaction = this, context = requireContext(), showArrow = true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = packingItemsAdapter
    }

    private fun navigateToMatchFoundFragment() {
        val action =
            FinaliseParentFragmentDirections.actionFinalisePackingFragmentToMatchFoundFragment2()
        findNavController().navigate(action)
    }

    private fun observeFinalisePackingEvents() {
        viewModel.finalisePackingFlow.onEach { state ->
            when (state) {

                is FinalisePackingViewState.Error -> {
                    progressBarManager.dismiss()
                    packingSharedViewModel.stockyardsListToSelectFrom = null
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                FinalisePackingViewState.Loading -> progressBarManager.show()



                is FinalisePackingViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()

                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (!state.isFromUserEntry) {
                            val matchingArticles =
                                getMatchingArticles(state.warehouseStockyardInventoryEntriesResponse)
                            if(matchingArticles.size>0) {
                                packingSharedViewModel.selectedArticle = matchingArticles[0]
                                packingSharedViewModel.stockyardsListToSelectFrom = matchingArticles
                                navigateToMatchFoundFragment()
                            }else{
                                showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
                            }
                        }
                    } else {
                        if (!state.isFromUserEntry) {
                            showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
                        }
                    }
                }


                is FinalisePackingViewState.GetPackingItemsResult -> {
                    progressBarManager.dismiss()
                    if (state.packingItems?.items.isNullOrEmpty()) {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.packingList.visibility = View.GONE

                    } else {
                        binding.emptyLayout.visibility = View.GONE
                        binding.packingList.visibility = View.VISIBLE
                    }
                    packingItemsAdapter.submitList(state.packingItems?.items?.filter { s -> s.packingListId == packingId })
                    packingSharedViewModel.packingItems =
                        state.packingItems?.items?.filter { s -> s.packingListId == packingId }
                    showBanner()
                }

                else -> {
                    progressBarManager.dismiss()
                }

            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showBanner() {

        if (countOpenItems() > 0)
            showInformationBanner(
                getString(
                    R.string.still_have_positions_pending_in_the_list,
                    countOpenItems().toString()
                )
            )
        else
            showPositiveBanner(getString(R.string.ready_for_packing))
    }

    private fun countOpenItems(): Int {
        return packingSharedViewModel.packingItems
            ?.count { item ->
                val shippingContainers = item.shippingContainers // Create a local reference
                (item.amount != item.packedAmount) || // Item has amount left to be packed
                        shippingContainers == null || // No shipping containers assigned
                        shippingContainers.none { it.packingListId == args.packingId } // No valid shipping container
            }
            ?: 0
    }

    private fun getMatchingArticles(warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>): ArrayList<WarehouseStockyardInventoryEntriesResponseModel> {
        // Create a set of articleIds from packingItems for faster lookup
        if (packingSharedViewModel.packingItems != null) {
            val packingItemArticleId = packingSharedViewModel.selectedPackingItemToPack?.articleId

            // Filter the warehouseStockyardInventoryEntriesResponse based on matching articleId
            return ArrayList(
                warehouseStockyardInventoryEntriesResponse.filter { it.articleId == packingItemArticleId }
            )
        }
        return warehouseStockyardInventoryEntriesResponse
    }




    override fun onViewClicked(data: PackingItem) {
        packingSharedViewModel.selectedPackingItemToPack = data

        viewModel.onEvent(
            FinalisePackingEvent.GetWarehouseStockyardInventoryEntries(
                articleId = data.articleId,
                stockyardId = "",
                warehouseCode = IncomingConstants.WarehouseParam,
                isFromUserEntry = false
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(FinalisePackingEvent.Empty)

    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


}



