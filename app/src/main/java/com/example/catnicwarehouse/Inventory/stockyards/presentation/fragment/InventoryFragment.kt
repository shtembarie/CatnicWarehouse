package com.example.catnicwarehouse.Inventory.stockyards.presentation.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.Inventory.stockyards.presentation.adapter.InventoryAdapter
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdEvent
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.Inventory.stockyards.presentation.viewModel.InventoryViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentInventoryBinding
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.fragment.UnloadingStockyardsFragmentDirections
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.inventory.model.GetInventoryByIdUIModelCurrentInventory
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

@AndroidEntryPoint
class InventoryFragment : BaseFragment(), ScanEventListener {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val inventoryViewModel: InventoryViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private var scannerHelper: ScannerHelper? = null
    private var scanType: ScanType = ScanType.STOCKYARD
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var currentInventory: GetInventoryByIdUIModelCurrentInventory? = null
    private var currentInventoryId: WarehouseStockyardsDTO? = null
    private lateinit var newDesignElement: LinearLayout
    private val SHOW_TIME = 10000L
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        observeNavigationResponse()
        setupInventoryBottomSheet()
        inventoryViewModel.onEvent(GetInventoryByIdEvent.LoadCurrentInventory(inventorySharedViewModel.warehouseCode))
        binding.inventoryList.layoutManager = LinearLayoutManager(requireContext())
        handleSuccessStockyardScanResultBack()
        handleScanOptionResultBack()
        //handleArgsUpdateFromMatchFoundResultBack()
        newDesignElement = binding.newDesignElement
        if (shouldShowNewDesignElement()) {
            showNewDesignElement()
        }
    }
    /**
     * Sets up the header section of the inventory screen.
     *
     * This function sets the header title to "Stock Yards" and makes the
     * toolbar section visible. It also sets a click listener on the left
     * toolbar button to finish the activity when pressed.
     */
    private fun handleHeaderSection() {
        binding.inventoryHeader.headerTitle.text = getString(R.string.stock_yards)
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().finish()
        }

    }
    /**
     * Displays the new design element for a limited time.
     *
     * This function makes the new design element visible and then schedules
     * it to be hidden after a predefined duration (SHOW_TIME) using a
     * Handler.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showNewDesignElement() {
        newDesignElement.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            newDesignElement.visibility = View.GONE
        }, SHOW_TIME)
    }
    /**
     * Determines whether the new design element should be shown.
     *
     * This function checks shared preferences to see if the user has already
     * seen the new design element. If they haven't, it records that they have
     * seen it and returns true. Otherwise, it returns false.
     *
     * @return true if the new design element should be shown; false otherwise.
     */
    private fun shouldShowNewDesignElement(): Boolean {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasSeen = prefs.getBoolean("has_seen_new_design_element", false)
        if (hasSeen) {
            return false
        } else {
            prefs.edit().putBoolean("has_seen_new_design_element", true).apply()
            return true
        }
    }
    /**
     * Observes the navigation response for inventory retrieval.
     *
     * This function listens for changes in the inventory retrieval state
     * from the inventoryViewModel. Based on the current state, it updates
     * the UI components accordingly, handling cases for loading, errors,
     * and successfully retrieved inventory data.
     */
    private fun observeNavigationResponse() {
        inventoryViewModel.getInventoryById.onEach { state ->
            when (state) {
                GetInventoryByIdViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is GetInventoryByIdViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                GetInventoryByIdViewState.Loading -> {
                    progressBarManager.show()
                    binding.newDesignElement.visibility = View.GONE
                    binding.scanStockyardsButton.visibility = View.GONE
                    binding.inventoryList.visibility = View.GONE
                }

                GetInventoryByIdViewState.Reset -> {
                    progressBarManager.dismiss()
                    binding.inventoryList.adapter = null
                }

                is GetInventoryByIdViewState.GetCurrentInventory -> {
                    progressBarManager.dismiss()
                    binding.inventoryList.visibility = View.VISIBLE
                    currentInventory = state.getCurrentInventory

                    inventorySharedViewModel.saveClickedStockyardId(currentInventory!!.id)
                    inventorySharedViewModel.saveInventoryItemIdsAndArticleIds(currentInventory!!.inventoryItems)
                    currentInventory?.let { inventory ->
                        inventorySharedViewModel.saveWarehouseCode(inventory.warehouseCode)
                    }
                    setupAdapter(currentInventory!!)
                }

                else -> {

                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    /**
     * Sets up the inventory list adapter with the current inventory data.
     *
     * This function initializes the InventoryAdapter with the stockyards
     * from the current inventory and defines the action to be performed
     * when a stockyard is selected. It also manages the visibility
     * of UI elements based on whether stockyards are available.
     *
     * @param currentInventory The current inventory data to populate the adapter.
     */
    private fun setupAdapter(currentInventory: GetInventoryByIdUIModelCurrentInventory) {
        val stockyards = currentInventory.warehouseStockYards
        val adapter = InventoryAdapter(
            stockyards,
         { stockyardId ->
            // Getting the stockyard name using the stockyardId
            val stockyardNames = getStockyardNamesByIds(listOf(stockyardId))
            val stockyardName = stockyardNames.firstOrNull() ?: ""

            val filteredItems =
                currentInventory.inventoryItems.filter { it.warehouseStockYardId == stockyardId }

            val action =
                InventoryFragmentDirections.actionInventoryFragmentToInventoryItemsFragment(
                    stockyardId,
                    filteredItems.toTypedArray(),
                    stockyardName,
                    stockyardId
                )
            findNavController().navigate(action)
        },
        inventorySharedViewModel
        )
        binding.inventoryList.adapter = adapter
        binding.noInventory.noStockyards.visibility =
            if (stockyards.isEmpty()) View.VISIBLE else View.GONE
        binding.newDesignElement.visibility = if (stockyards.isEmpty()) View.GONE else View.VISIBLE
        binding.scanStockyardsButton.visibility =
            if (stockyards.isEmpty()) View.GONE else View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Resetting the state when leaving the fragment, so it shows again when the fragment is recreated.
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_new_design_element", false).apply()
        //scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
    }
    override fun onResume() {
        super.onResume()
    }
    /**
     * Sets up click listeners for the inventory bottom sheet buttons.
     *
     * This function initializes click listeners for buttons that allow the user
     * to proceed with scanning stockyards or continue with inventory actions.
     */
    private fun setupInventoryBottomSheet() {
        binding.buttonContinue.setOnClickListener {
            showScanOptionsBottomSheet(scanType)
        }
        binding.scanStockyardsButton.setOnClickListener {
            showScanOptionsBottomSheet(scanType)
        }
    }
    /**
     * Displays the scan options bottom sheet.
     *
     * This function creates and shows the ScanOptionsBottomSheet with the specified
     * scan type, allowing the user to choose how they would like to scan.
     *
     * @param scanType The type of scan to be performed (e.g., barcode, camera).
     */
    private fun showScanOptionsBottomSheet(scanType: ScanType) {
        scannerHelper?.stopScanning()

        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }
    /**
     * Sets up a listener to receive results from the Stockyard scan process.
     *
     * This function registers a result listener using the parent fragment manager
     * to handle successful stockyard scans. When a result is received, it retrieves
     * the title text, stockyard ID, and scan type from the provided Bundle,
     * and then delegates the handling to the `handleSuccessStockyardScanResultBackAction` function.
     */
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
    private fun getStockyardNamesByIds(stockyardId: List<Int>): List<String> {
        val stockyards = inventorySharedViewModel.stockyards
        val stockyardNames = stockyardId.map { id ->
            stockyards?.find { it?.id == id }?.name ?: ""
        }
        return stockyardNames
    }
    /**
     * Handles the actions to be performed after a successful stockyard scan.
     *
     * This function processes the results based on the scan type. If the scan type is STOCKYARD,
     * it checks if the provided stockyard ID is valid and retrieves corresponding stockyard names.
     * It then filters the current inventory for items associated with that stockyard ID and
     * presents a bottom sheet dialog with options to continue picking items or scan again.
     * If the scan type is ONLY_STOCKYARD, it navigates directly to the inventory items fragment.
     *
     * @param titleText The title text to display in the bottom sheet.
     * @param stockyardId The ID of the scanned stockyard.
     * @param scanType The type of scan that was performed.
     */

    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        stockyardId: Int?,
        scanType: ScanType?
    ) {
        if (scanType == ScanType.STOCKYARD) {
            if (titleText == null || stockyardId == null) {
                showErrorBottomSheet()
                return
            }

            val stockyardName = getStockyardNamesByIds(listOf(stockyardId)).firstOrNull() ?: ""
            val filteredItems = currentInventory?.inventoryItems
                ?.filter { it.warehouseStockYardId == stockyardId } ?: emptyList()

            if (filteredItems.isNotEmpty()) {
                showSuccessBottomSheet(stockyardName, stockyardId, filteredItems)
            } else {
                showErrorBottomSheet()
            }
        } else if (scanType == ScanType.ONLY_STOCKYARD) {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryItemsFragment)
        }
    }

    private fun showSuccessBottomSheet(
        stockyardName: String,
        stockyardId: Int,
        filteredItems: List<InventoryItem>
    ) {
        val bottomSheet = SuccessScanBottomSheet.newInstance(
            titleText = stockyardName,
            isIconVisible = false,
            descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
            button1Text = getString(R.string.yes_pick_stockyard),
            button2Text = getString(R.string.scan_again),
            button1Callback = {
                val action = InventoryFragmentDirections.actionInventoryFragmentToInventoryItemsFragment(
                    stockyardId,
                    filteredItems.toTypedArray(),
                    stockyardName,
                    stockyardId
                )
                findNavController().navigate(action)
            },
            button2Callback = {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun showErrorBottomSheet() {
        val errorBottomSheet = ErrorScanBottomSheet.newInstance(
            titleText = getString(R.string.unknown_bar_code),
            descriptionText = getString(R.string.popup_description),
            buttonText = getString(R.string.scan_again),
            button1Callback = {
                scannerHelper?.stopScanning()
                val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
            }
        )
        errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
    }
    /**
     * Sets up a listener to handle scan option results from the scan option bottom sheet.
     *
     * This function registers a result listener that reacts to the selection made in the
     * scan option bottom sheet. Depending on the chosen scan option (barcode, camera, or manual),
     * it initiates the appropriate scanning method or manual input process.
     */
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
    /**
     * Opens the manual input bottom sheet for user input.
     *
     * This function creates an instance of the ManualInputBottomSheet, passing the necessary
     * scan type and module type. It also sets a listener to nullify the reference when dismissed.
     *
     * @param scanType The type of scan being performed (e.g., ARTICLE or STOCKYARD).
     */
    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.INVENTORY
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
    }
    /**
     * Initiates the scanner based on the selected scanner type.
     *
     * This function checks if the EMDK is available on the device. If not, it displays a toast message.
     * If the scannerHelper is not already initialized, it creates a new instance; otherwise, it updates
     * the scanner type of the existing instance. Finally, it starts the scanning process.
     *
     * @param scannerType The type of scanner to be used (DEFAULT_SCANNER or CAMERA).
     */
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
            scanEventListener = this@InventoryFragment
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
    /**
     * Handles incoming data from the scanner based on the scan type.
     *
     * This function processes the scanned data and updates the inventory based on
     * the current scan type. For STOCKYARD and ONLY_STOCKYARD scan types, it retrieves
     * the stockyard ID from the scanned data and displays a success or error message
     * based on whether items are found in the inventory. For ARTICLE scan type,
     * it triggers an event to search for the article by ID.
     *
     * @param data The scanned data received from the scanner.
     */
    private fun updateData(data: String) {
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null
        scanPopupFragment?.dismissPopup()
        when (scanType) {
            ScanType.ONLY_STOCKYARD, ScanType.STOCKYARD -> {
                val stockyardId = data.toIntOrNull()
                if (stockyardId != null) {
                    val stockyardName = getStockyardNamesByIds(listOf(stockyardId)).firstOrNull() ?: ""
                    val filteredItems = currentInventory?.inventoryItems
                        ?.filter { it.warehouseStockYardId == stockyardId } ?: emptyList()
                    if (filteredItems.isNotEmpty()) {
                        // Show success bottom sheet before navigation
                        showSuccessBottomSheet(
                            stockyardName = stockyardName,
                            stockyardId = stockyardId,
                            filteredItems = filteredItems
                        )
                    } else {
                        // Handle case where the stockyard has no items
                        showErrorBottomSheet()
                    }
                } else {
                    // Handle invalid or unrecognized stockyard ID
                    showErrorBottomSheet()
                }
            }

            ScanType.ARTICLE -> {
                inventoryViewModel.onEvent(GetInventoryByIdEvent.SearchArticle(data))
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
}