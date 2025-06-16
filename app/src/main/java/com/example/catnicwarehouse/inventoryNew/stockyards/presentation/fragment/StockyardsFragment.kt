package com.example.catnicwarehouse.inventoryNew.stockyards.presentation.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentStockyardsBinding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.adapter.InventoryStockyardsAdapter
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses.StockyardEvent
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses.StockyardsViewState
import com.example.catnicwarehouse.inventoryNew.stockyards.presentation.viewModel.StockyardsViewModel
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.enums.ScanType.ARTICLE
import com.example.catnicwarehouse.scan.presentation.enums.ScanType.ONLY_STOCKYARD
import com.example.catnicwarehouse.scan.presentation.enums.ScanType.STOCKYARD
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.symbol.emdk.barcode.StatusData.ScannerStates
import com.symbol.emdk.barcode.StatusData.ScannerStates.DISABLED
import com.symbol.emdk.barcode.StatusData.ScannerStates.ERROR
import com.symbol.emdk.barcode.StatusData.ScannerStates.IDLE
import com.symbol.emdk.barcode.StatusData.ScannerStates.SCANNING
import com.symbol.emdk.barcode.StatusData.ScannerStates.WAITING
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val SHOW_TIME = 10000L

@AndroidEntryPoint
class StockyardsFragment : BaseFragment(), ScanEventListener {

    private var _binding: FragmentStockyardsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StockyardsViewModel by viewModels()
    private lateinit var stockyardsAdapter: InventoryStockyardsAdapter
    private var currentInventory: CurrentInventoryResponseModel? = null

    private var scanType: ScanType = STOCKYARD
    private var scannerHelper: ScannerHelper? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockyardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        setupAdapter()
        viewModel.onEvent(StockyardEvent.GetCurrentInventory(IncomingConstants.WarehouseParam))
        observeStockyardsEvents()

        handleScanStockyardButtonAction()
        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from stockyard scan success
        handleSuccessStockyardScanResultBack()
        if (shouldShowBanner()) {
            showBanner()
        }
    }

    private fun observeStockyardsEvents() {
        viewModel.stockyardsFlow.onEach { state ->
            when (state) {
                StockyardsViewState.Empty -> progressBarManager.dismiss()
                is StockyardsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(it) }
                }

                is StockyardsViewState.GetCurrentInventoriesResult -> {
                    progressBarManager.dismiss()
                    currentInventory = state.currentInventory
                    val stockyards =
                        state.currentInventory?.warehouseStockYards?.filter { s -> s.inventoried }
                    stockyardsAdapter.submitList(stockyards)

                    binding.recyclerViewInventory.visibility =
                        if (stockyards.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.noInventory.noStockyards.visibility =
                        if (stockyards.isNullOrEmpty()) View.VISIBLE else View.GONE
                    binding.newDesignElement.visibility =
                        if (stockyards.isNullOrEmpty()) View.GONE else View.VISIBLE
                    binding.scanStockyardsButton.visibility =
                        if (stockyards.isNullOrEmpty()) View.GONE else View.VISIBLE
                }

                StockyardsViewState.Loading -> progressBarManager.show()
                StockyardsViewState.Reset -> progressBarManager.dismiss()
                is StockyardsViewState.GetWarehouseStockyardsByWarehouseCodeResult -> {

                }

                is StockyardsViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {
                        if (state.fromUserInteraction) {
                            inventorySharedViewModel.filteredInventoryItems =
                                currentInventory?.inventoryItems?.filter { inventoryItem -> inventoryItem.warehouseStockYardId == state.warehouseStockyard.id }
                            inventorySharedViewModel.scannedStockyard = state.warehouseStockyard
                            inventorySharedViewModel.selectedInventoryId = currentInventory?.id
                            navigateToArticlesFragment()
                        } else {
                            val action =
                                StockyardsFragmentDirections.actionInventoryFragmentToStockyardTreeFragment3()
                            action.selectedStockyardId = state.warehouseStockyard.id.toString()
                            action.scanType = scanType.type
                            action.moduleType = ModuleType.INVENTORY.type
                            findNavController().navigate(action)
                        }

                    }
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleHeaderSection() {
        binding.inventoryHeader.headerTitle.text = getString(R.string.stock_yards)
        binding.inventoryHeader.leftToolbarButton.visibility = View.VISIBLE
        binding.inventoryHeader.rightToolbarButton.visibility = View.GONE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showBanner() {
        binding.newDesignElement.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            if (isVisible && isResumed)
                binding.newDesignElement.visibility = View.GONE
        }, SHOW_TIME)
    }

    private fun shouldShowBanner(): Boolean {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasSeen = prefs.getBoolean("has_seen_new_design_element", false)
        if (hasSeen) {
            return false
        } else {
            prefs.edit().putBoolean("has_seen_new_design_element", true).apply()
            return true
        }
    }

    private fun setupAdapter() {

        stockyardsAdapter = InventoryStockyardsAdapter { stockyard ->
            viewModel.onEvent(
                StockyardEvent.GetWarehouseStockyardById(
                    id = stockyard.id.toString(),
                    isFromUserInteraction = true
                )
            )
        }
        binding.recyclerViewInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewInventory.adapter = stockyardsAdapter
    }

    private fun handleScanStockyardButtonAction() {
        binding.scanStockyardsButton.setOnClickListener {
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.scanStockyardsTextView.setOnClickListener {
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun handleScanOptionResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "scanOptionBottomSheet", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            val scanOption = bundle.getParcelable<ScanOptionEnum>("scanOption")!!

            if (scanOption == ScanOptionEnum.BARCODE) {
                scannerType = ScannerType.DEFAULT_SCANNER
                openScanner(scannerType!!)
            } else if (scanOption == ScanOptionEnum.CAMERA) {
                scannerType = ScannerType.CAMERA
                openScanner(scannerType!!)
            } else if (scanOption == ScanOptionEnum.MANUAL) {
                scannerType = null
                openManualInputBottomSheet(scanType)
            }
        }
    }

    private fun handleSuccessStockyardScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessStockyardScan", viewLifecycleOwner
        ) { _, bundle ->
            val titleText = bundle.getString("titleText")
            val scanType = bundle.getParcelable<ScanType>("scanType")
            val stockyardId = bundle.getInt("stockyardId")
            handleSuccessStockyardScanResultBackAction(titleText, scanType, stockyardId)

        }
    }

    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?, scanType: ScanType?, stockyardId: Int
    ) {
        if (scanType == STOCKYARD) {
            titleText?.let {
                val bottomSheet = SuccessScanBottomSheet.newInstance(titleText = it,
                    isIconVisible = false,
                    descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                    button1Text = getString(R.string.yes_scan_articles),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        inventorySharedViewModel.filteredInventoryItems =
                            currentInventory?.inventoryItems?.filter { inventoryItem -> inventoryItem.warehouseStockYardId == stockyardId }
                        inventorySharedViewModel.selectedInventoryId = currentInventory?.id
                        navigateToArticlesFragment()
                    },
                    button2Callback = {
                        scannerHelper?.stopScanning()
                        val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    })
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        }
    }


    private fun openScanner(scannerType: ScannerType) {
        if (!requireActivity().isEMDKAvailable()) {
            Toast.makeText(
                requireContext(), "Zebra SDK is not available for this device", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (scannerHelper == null) scannerHelper = ScannerHelper(context = requireContext(),
            scannerType = scannerType,
            updateStatus = { status, scannerState ->
                updateStatus(
                    status = status,
                    scannerStates = scannerState
                )
            },
            updateData = { data ->
                updateData(
                    data = data,
                )
            })
        scannerHelper?.changeScannerType(scannerType)
        if (scannerType == ScannerType.DEFAULT_SCANNER) scannerHelper?.let { openScanActivePopup() }
        else scannerHelper?.startScanning()
    }

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@StockyardsFragment
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

    private fun updateStatus(status: String, scannerStates: ScannerStates) {

        if (scanPopupFragment != null && scanPopupFragment!!.isAdded && scanPopupFragment!!.isResumed) {
            if (scannerStates == WAITING || scannerStates == IDLE) {
                if (isScanUIButtonPressed) {
                    scanPopupFragment?.updateTitle(getString(R.string.scanning))
                } else {
                    scanPopupFragment?.updateTitle(getString(R.string.scanner_is_ready))
                }
            } else if (scannerStates == SCANNING) {
                scanPopupFragment?.updateTitle(getString(R.string.scanning))
            } else if (scannerStates == DISABLED) {
                scanPopupFragment?.updateTitle(getString(R.string.scanner_is_disabled))
            } else if (scannerStates == ERROR) {
                scanPopupFragment?.updateTitle(getString(R.string.error_occured_while_scanning))
            }

        }

    }


    private fun updateData(data: String) {
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null
        scanPopupFragment?.dismissPopup()
        when (scanType) {
            ONLY_STOCKYARD, STOCKYARD -> {
                viewModel.onEvent(
                    StockyardEvent.GetWarehouseStockyardById(
                        id = data,
                        isFromUserInteraction = false
                    )
                )
            }

            ARTICLE -> {

            }
        }
    }

    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType, moduleType = ModuleType.INVENTORY
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
    }


    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun navigateToArticlesFragment() {
        val action =
            StockyardsFragmentDirections.actionInventoryFragmentToArticlesFragment2()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(StockyardEvent.Reset)
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
    }

    override fun onScanActionDown() {
        isScanUIButtonPressed = true
        scannerHelper?.startScanning()
    }

    override fun onScanActionUp() {
        isScanUIButtonPressed = false
        scannerHelper?.stopScanning()
    }


}