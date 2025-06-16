package com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.adapter.CorrectingStockStockYardAdapter
import com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.adapter.CorrectingStockStockYardAdapterInteraction
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses.GetWarehouseStockYardEvent
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses.GetWarehouseStockYardViewState
import com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.viewModel.WarehouseStockYardViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentWarehousesStockyardsBinding
import com.example.catnicwarehouse.movement.articles.presentation.adapter.ArticleSelectionAdapter
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.correctingStock.model.GetCorrectionByIdUIModelCurrentInventory
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.symbol.emdk.barcode.StatusData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WarehouseStockyardsFragments : BaseFragment(), ScanEventListener {
    private var _binding: FragmentWarehousesStockyardsBinding? = null
    private val binding get() = _binding!!
    private val warehouseStockYardViewModel: WarehouseStockYardViewModel by activityViewModels()
    private val corrStockSharedViewModel: CorrStockSharedViewModel by activityViewModels()
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleSelectionAdapter
    // Adapter with interaction handling
    private lateinit var stockYardAdapter: CorrectingStockStockYardAdapter
    private var scanType: ScanType = ScanType.STOCKYARD
    private var currentInventory: List<GetCorrectionByIdUIModelCurrentInventory>? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var isScanUIButtonPressed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWarehousesStockyardsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        isItemCorrected()
        handleHeaderSection()
        observeNavigationResponse()
        setupInventoryBottomSheet()
        handleSuccessStockyardScanResultBack()
        handleScanOptionResultBack()
        warehouseStockYardViewModel.onEvent(GetWarehouseStockYardEvent.Loading(corrStockSharedViewModel.warehouseCode))
    }
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun isItemCorrected(){
        // Show the new design element
        binding.newDesignElement.visibility = View.VISIBLE
        // Set an OnTouchListener to hide the view when the user touches it
        binding.newDesignElement.setOnTouchListener { _, _ ->
            // Hide the element when touched
            binding.newDesignElement.visibility = View.GONE
            true // Return true to indicate the touch event was handled
        }
    }
    private fun setAdapter(){
        // Initialize the adapter with interaction and context
        stockYardAdapter = CorrectingStockStockYardAdapter(
            interaction = object : CorrectingStockStockYardAdapterInteraction {
                override fun onStockyardClicked(stockyard: GetCorrectionByIdUIModelCurrentInventory) {
                    // Handle item click event
                    // For example, navigate to another fragment or perform an action
                    val action = WarehouseStockyardsFragmentsDirections
                        .actionWarehouseStockyardsFragmentsToArticlesCorrectingStockFragment(stockyard.id)

                    findNavController().navigate(action)
                }
            },
            context = requireContext(),
            corrStockSharedViewModel
        )

        // Set up RecyclerView
        binding.stockyardsList.layoutManager = LinearLayoutManager(requireContext())
        binding.stockyardsList.adapter = stockYardAdapter
    }
    private fun setupAdapter(stockyards: List<GetCorrectionByIdUIModelCurrentInventory>) {
        // Submit the list to the adapter
        stockYardAdapter.submitList(stockyards)

        // Show or hide the scan button based on the stockyards list
        binding.scanStockyardsButton.visibility = if (stockyards.isEmpty()) View.GONE else View.VISIBLE
    }
    private fun handleHeaderSection(){
        binding.inventoryHeader.headerTitle.text = getString(R.string.stockyards)
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().finish()
        }
    }
    private fun observeNavigationResponse() {
        warehouseStockYardViewModel.getWarehouseStockYard.onEach { state ->
            when (state) {

                GetWarehouseStockYardViewState.Empty -> {
                    progressBarManager.dismiss()
                }
                is GetWarehouseStockYardViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage ) }
                    binding.stockyardsList.visibility = View.GONE
                    binding.constraintLayout.visibility = View.VISIBLE
                }
                GetWarehouseStockYardViewState.Loading -> {
                    progressBarManager.show()
                    binding.stockyardsList.visibility = View.GONE
                    binding.scanStockyardsButton.visibility = View.GONE
                    binding.inventoryHeader.rightToolbarButton.visibility = View.GONE
                    binding.stockyardsList.visibility = View.GONE
                    binding.constraintLayout.visibility = View.GONE
                    binding.newDesignElement.visibility = View.GONE
                }
                GetWarehouseStockYardViewState.Reset -> {
                    progressBarManager.dismiss()
                }
                is GetWarehouseStockYardViewState.WarehouseStockFound -> {
                    progressBarManager.dismiss()
                    val stockyards = state.warehouseStockyard
                    if (stockyards.isEmpty()) {
                        // Show empty layout if no stockyards
                        binding.stockyardsList.visibility = View.GONE
                        binding.constraintLayout.visibility = View.VISIBLE
                        binding.newDesignElement.visibility = View.GONE
                    } else {
                        // Show RecyclerView and hide empty layout
                        binding.stockyardsList.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.GONE
                        binding.newDesignElement.visibility = View.VISIBLE
                        val stockyards = state.warehouseStockyard
                        currentInventory = stockyards // Update currentInventory
                        setupAdapter(stockyards)
                    }
                }
                is GetWarehouseStockYardViewState.ArticlesForInventoryFound -> {
                    progressBarManager.dismiss()
                }
                is GetWarehouseStockYardViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

    }
    /** Implementing the Scanner Device */
    private fun setupInventoryBottomSheet() {
        binding.scanStockyardsTextView.setOnClickListener {
            //scannerHelper?.stopScanning()
            showScanOptionsBottomSheet(scanType)
        }
        binding.scanStockyardsButton.setOnClickListener {
            //scannerHelper?.stopScanning()
            showScanOptionsBottomSheet(scanType)
        }
    }
    private fun showScanOptionsBottomSheet(scanType: ScanType) {
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }
    private fun handleSuccessStockyardScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessStockyardScan", viewLifecycleOwner
        ) { _, bundle ->
            val titleText = bundle.getString("titleText")
            val stockyardId = bundle.getInt("stockyardId")
            val scanType = bundle.getParcelable<ScanType>("scanType")
            handleSuccessStockyardScanResultBackAction(titleText, stockyardId, scanType)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        stockyardId: Int?,
        scanType: ScanType?
    ) {
        if (scanType == ScanType.STOCKYARD) {
            titleText?.let { stockyardIdString ->

                if (stockyardId != null) {
                    val filteredItems = currentInventory
                        ?.filter { it.id == stockyardId } ?: emptyList()// Match based on stockyardId,

                        if (filteredItems.isNotEmpty()) {
                            val stockyardName = filteredItems.firstOrNull()?.name ?: "Stockyard"
                            val bottomSheet = SuccessScanBottomSheet.newInstance(
                                titleText = stockyardName,
                                descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                button1Text = getString(R.string.yes_pick_stockyard),
                                button2Text = getString(R.string.scan_again),
                                button1Callback = {
                                    // Navigating to the WarehouseStockyardsFragments with the filtered items and the stockyardNames
                                    val action =
                                        WarehouseStockyardsFragmentsDirections.actionWarehouseStockyardsFragmentsToArticlesCorrectingStockFragment(
                                            stockyardId,
                                        )
                                    findNavController().navigate(action)
                                },
                                button2Callback = {
                                    val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                })
                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                        }
                }
            }
        } else if (scanType == ScanType.ONLY_STOCKYARD) {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryItemsFragment)
        }
    }
    private fun handleScanOptionResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "scanOptionBottomSheet", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            when (bundle.getParcelable<ScanOptionEnum>("scanOption")!!) {
                ScanOptionEnum.BARCODE -> {
                    openScanner(ScannerType.DEFAULT_SCANNER)
                }

                ScanOptionEnum.CAMERA -> {
                    openScanner(ScannerType.CAMERA)
                }

                ScanOptionEnum.MANUAL -> {
                    openManualInputBottomSheet(scanType)
                }
            }
        }
    }
    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.CORRECTIVE_STOCK
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
    }
    private fun openScanner(scannerType: ScannerType) {
        if (!requireActivity().isEMDKAvailable()) {
            Toast.makeText(
                requireContext(), "Zebra SDK is not available for this device", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (scannerHelper == null)
            scannerHelper = ScannerHelper(
                context = requireContext(),
                scannerType = scannerType,
                updateStatus = { status,scannerState -> updateStatus(status,scannerState) },
                updateData = { data -> updateData(data) }
            )
        else {
            scannerHelper?.changeScannerType(scannerType)
        }
        if (scannerType == ScannerType.DEFAULT_SCANNER) scannerHelper?.let { openScanActivePopup() }
        else
            scannerHelper?.startScanning()
    }
    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@WarehouseStockyardsFragments
            onManualInputClick = {
                dismissPopup()
                openManualInputBottomSheet(scanType)
            }
            onCancelClick = {
                dismissPopup()
            }
            onActiveScanClick = { event ->
                scannerHelper?.handleScanButton(event)
            }

        }

        scanPopupFragment?.show(parentFragmentManager, "ScanPopupFragment")
    }
    @SuppressLint("SuspiciousIndentation")
    private fun updateStatus(status: String, scannerStates: StatusData.ScannerStates) {

        if (scanPopupFragment != null && scanPopupFragment!!.isAdded && scanPopupFragment!!.isResumed) {
            when (scannerStates) {
                StatusData.ScannerStates.WAITING, StatusData.ScannerStates.IDLE -> {
                    if (isScanUIButtonPressed) {
                        scanPopupFragment?.updateTitle(
                            getString(R.string.scanning)
                        )
                    } else {
                        scanPopupFragment?.updateTitle(
                            getString(R.string.scanner_is_ready)
                        )
                    }
                }

                StatusData.ScannerStates.SCANNING -> scanPopupFragment?.updateTitle(
                    getString(R.string.scanning)
                )

                StatusData.ScannerStates.DISABLED -> scanPopupFragment?.updateTitle(getString(R.string.scanner_is_disabled))
                StatusData.ScannerStates.ERROR -> scanPopupFragment?.updateTitle(getString(R.string.error_occured_while_scanning))
            }
        }

    }
    @SuppressLint("SuspiciousIndentation")
    private fun updateData(data: String) {
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null
        when (scanType) {
            ScanType.ONLY_STOCKYARD, ScanType.STOCKYARD -> {
                val stockyardId = data.toIntOrNull()
                if (stockyardId != null) {
                    val filteredItems = currentInventory
                        ?.filter { it.id == stockyardId }  ?: emptyList() // Match based on stockyardId
                        if (filteredItems.isNotEmpty()) {
                            val stockyardName = filteredItems.firstOrNull()?.name ?: "Stockyard"

                            val bottomSheet = SuccessScanBottomSheet.newInstance(
                                titleText = stockyardName,
                                descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                button1Text = getString(R.string.yes_pick_stockyard),
                                button2Text = getString(R.string.scan_again),
                                button1Callback = {
                                    val action =
                                        WarehouseStockyardsFragmentsDirections.actionWarehouseStockyardsFragmentsToArticlesCorrectingStockFragment(
                                            stockyardId,
                                        )
                                    findNavController().navigate(action)
                                },
                                button2Callback = {
                                    val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            )
                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                        } else {
                            val errorBottomSheet = ErrorScanBottomSheet.newInstance(
                                titleText = getString(R.string.unknown_bar_code),
                                descriptionText = getString(R.string.popup_description),
                                buttonText = getString(R.string.scan_again),
                                button1Callback = {
                                    val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                                    scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
                                }
                            )
                            errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
                        }

                } else {
                    val errorBottomSheet = ErrorScanBottomSheet.newInstance(
                        titleText = getString(R.string.unknown_bar_code),
                        descriptionText = getString(R.string.popup_description),
                        buttonText = getString(R.string.scan_again),
                        button1Callback = {
                            val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                            scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
                        }
                    )
                    errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
                }
            }

            ScanType.ARTICLE -> {
                warehouseStockYardViewModel.onEvent(GetWarehouseStockYardEvent.SearchArticle(data))
            }
        }
    }
    override fun onScanActionDown() {
        isScanUIButtonPressed = true
        scannerHelper?.startScanning()
    }

    override fun onScanActionUp() {
        isScanUIButtonPressed = false
        scannerHelper?.stopScanning()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scanPopupFragment?.dismissPopup()
    }
}
