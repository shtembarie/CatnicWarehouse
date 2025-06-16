package com.example.catnicwarehouse.scan.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.StockyardTreeFragmentBinding
import com.example.catnicwarehouse.incoming.matchFound.presentation.fragment.MatchFoundFragmentArgs
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.scan.presentation.adapter.MultiLevelAdapter
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.helper.OnItemSelectListener
import com.example.catnicwarehouse.scan.presentation.helper.getFullPath
import com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree.StockyardTreeEvent
import com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree.StockyardTreeViewState
import com.example.catnicwarehouse.scan.presentation.viewModel.StockyardTreeViewModel
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StockyardTreeFragment : BaseFragment(), OnItemSelectListener {

    private var _binding: StockyardTreeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var multiLevelAdapter: MultiLevelAdapter

    private val viewModel: StockyardTreeViewModel by viewModels()
    private val args: StockyardTreeFragmentArgs by navArgs()
    private var selectedStockyardId = 0
    private var scanType = ScanType.STOCKYARD.name
    private var moduleType = ModuleType.MOVEMENTS.type
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StockyardTreeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        selectedStockyardId  = args.selectedStockyardId.toInt()
        scanType = args.scanType
        moduleType = args.moduleType
        viewModel.onEvent(StockyardTreeEvent.SearchWarehouseStockyards(warehouseCode = IncomingConstants.WarehouseParam, isFromUserSearch = false))
        observeStockyardTreeEvents()
    }

    private fun observeStockyardTreeEvents() {
        viewModel.stockyardTreeFlow.onEach {state->
            when(state){
                StockyardTreeViewState.Empty -> progressBarManager.dismiss()
                is StockyardTreeViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                StockyardTreeViewState.Loading -> progressBarManager.show()
                StockyardTreeViewState.Reset -> progressBarManager.dismiss()
                is StockyardTreeViewState.WarehouseStockyardsFound -> {
                    progressBarManager.dismiss()
                    state.warehouseStockyards?.let { setUpAdapter(it) }
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.select_stockyard)
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
        handleBackPress()
    }


    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun buildHierarchy(items: List<WarehouseStockyardsDTO>): List<WarehouseStockyardsDTO> {
        // Sort items by hierarchyLevel to ensure correct processing order
        val sortedItems = items.sortedBy { it.hierarchyLevel }

        // Create a map to hold each item and its corresponding children
        val itemMap = mutableMapOf<Int, WarehouseStockyardsDTO>()

        // Prepare a list for root items (those with no parentStockId)
        val rootItems = mutableListOf<WarehouseStockyardsDTO>()

        // First, populate the itemMap with all items
        sortedItems.forEach { item ->
            itemMap[item.id] =
                item.copy(children = mutableListOf()) // Make a copy with initialized children
        }

        // Now, assign children to their respective parents
        sortedItems.forEach { item ->
            val currentItem = itemMap[item.id]!!
            val parent = itemMap[item.parentStockId]
            if (parent != null) {
                // Add this item as a child to its parent
                itemMap[item.id]!!.parent = parent
                parent.children.add(itemMap[item.id]!!)

            } else {
                // If no parentStockId, it is a root item
                rootItems.add(itemMap[item.id]!!)
            }
        }

        return rootItems
    }

    private fun setUpAdapter(warehouseStockyards: List<WarehouseStockyardsDTO>) {
        val hierarchyList = buildHierarchy(warehouseStockyards)
        multiLevelAdapter = MultiLevelAdapter(hierarchyList,selectedStockyardId,this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rView.layoutManager = layoutManager
        binding.rView.adapter = multiLevelAdapter
    }

    override fun onItemSelected(item: WarehouseStockyardsDTO) {

        if(moduleType == ModuleType.MOVEMENTS.type){
            movementSharedViewModel.scannedStockyard = item
        }
        //In case of Packing
        else if (moduleType == ModuleType.PACKING_1.type) {
            packingSharedViewModel.scannedStockyard = item
        }
        //In case of packing drop zone
        else if (moduleType == ModuleType.PACKING_2.type) {
            packingSharedViewModel.dropzoneScannedStockyardId =
                item.id
        }

        else if(moduleType == ModuleType.INVENTORY.type){
            inventorySharedViewModel.scannedStockyard =  item
        }

        //In case of Incoming
        parentFragmentManager.setFragmentResult(
            "handleSuccessStockyardScan",
            bundleOf(
                "titleText" to "${item.name}",
                "stockyardId" to item.id,
                "scanType" to ScanType.fromType(scanType)
            )
        )
        findNavController().popBackStack()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressBarManager.dismiss()
        viewModel.onEvent(StockyardTreeEvent.Reset)
    }
}