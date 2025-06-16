package com.example.catnicwarehouse.dashboard.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentHomeBinding
import com.example.catnicwarehouse.dashboard.presentation.adapter.DashboardAdapter
import com.example.catnicwarehouse.dashboard.presentation.adapter.DashboardListInteraction
import com.example.catnicwarehouse.dashboard.presentation.adapter.model.DashBoardModel
import com.example.catnicwarehouse.incoming.shared.presentation.activity.IncomingActivity
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.dashboard.presentation.adapter.WarehouseAdapter
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardEvent
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardViewState
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.movement.movementList.presentation.activity.MovementsActivity
import com.example.catnicwarehouse.movement.shared.MovementStatus
import com.example.catnicwarehouse.packing.shared.presentation.activity.PackingActivity
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.catnicwarehouse.CorrectingStock.shared.CorrectingStockActivity
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.dashboard.presentation.bottomSheet.CheckAvailabilityBottomSheet
import com.example.catnicwarehouse.dashboard.presentation.bottomSheet.ItemOutOfStockBottomSheet
import com.example.catnicwarehouse.dashboard.presentation.bottomSheet.NoInventoryBottomSheet
import com.example.catnicwarehouse.defectiveItems.shared.activity.DefectiveItemsActivity
import com.example.catnicwarehouse.inventoryNew.shared.activity.InventoryActivity
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
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardFragment : BaseFragment(), DashboardListInteraction,
    CheckAvailabilityBottomSheet.CheckAvailabilityListener, ScanEventListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashBoardViewModel by viewModels()
    private lateinit var checksAvailabilityBottomSheet: CheckAvailabilityBottomSheet

    @Inject
    lateinit var dashboardAdapter: DashboardAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()

    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false
    private lateinit var dashboardList: List<DashBoardModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dashboardList = listOf(
            DashBoardModel(getString(R.string.incoming), R.drawable.ic_incoming),
            DashBoardModel(getString(R.string.movements), R.drawable.ic_movement),
            DashBoardModel(getString(R.string.packing), R.drawable.ic_packing),
            DashBoardModel(getString(R.string.inventory), R.drawable.inventory),
            DashBoardModel(getString(R.string.defective_items), R.drawable.defective_items),
            DashBoardModel(getString(R.string.corrective_stock), R.drawable.ic_correcting_stock),
            DashBoardModel(getString(R.string.checks), R.drawable.checks_dashboard_icon)
        )

        binding.homeHeader.warehouseSpinner.visibility = View.VISIBLE
        checksSharedViewModel.initViewModel()
        viewModel.onEvent(DashboardEvent.GetWarehouses)
        viewModel.onEvent(DashboardEvent.GetRights)
        setUpAdapter()
        observeEvents()
        handelHeaderSection()
        handleScanOptionResultBack()
        handleSuccessArticleScanResultBack()
        handleSuccessStockyardScanResultBack()

        return binding.root
    }

    private fun observeEvents() {
        viewModel.dashboardFlow.onEach { state ->
            when (state) {
                DashboardViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is DashboardViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is DashboardViewState.GetMovementsResult -> {
                    progressBarManager.dismiss()

                    if (!state.movements?.filter { s -> s.status == MovementStatus.OPEN.name }
                            .isNullOrEmpty()) {
                        val intent = Intent(
                            requireActivity(),
                            MovementsActivity::class.java
                        ).apply {
                            putExtra("hasMovements", true)
                        }
                        requireActivity().startActivity(
                            intent
                        )
                    } else {
                        viewModel.onEvent(DashboardEvent.CreateMovement(null))
                    }
                }

                DashboardViewState.Loading -> progressBarManager.show()
                DashboardViewState.Reset -> progressBarManager.dismiss()
                is DashboardViewState.CreateMovementResult -> {
                    progressBarManager.dismiss()
                    val intent = Intent(
                        requireActivity(),
                        MovementsActivity::class.java
                    ).apply {
                        putExtra("hasMovements", true)
                    }
                    requireActivity().startActivity(
                        intent
                    )
                }

                is DashboardViewState.GetWarehousesResult -> {
                    progressBarManager.dismiss()
                    state.warehouses?.let { warehouseSpinnerAdapter(it) }
                }

                is DashboardViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()
                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (state.isFromUserEntry) {

                            if (scanType == ARTICLE) {
                                val matchingArticles =
                                    getMatchingArticlesWithNotEmptyAmount(
                                        checksSharedViewModel.scannedArticle?.articleId,
                                        state.warehouseStockyardInventoryEntriesResponse
                                    )
                                if (matchingArticles.isNotEmpty()) {
                                    checksSharedViewModel.articlesListToSelectFrom =
                                        matchingArticles
                                    findNavController().navigate(R.id.checksArticlesListFragment)
                                } else {
                                    showOutOfStockBottomSheet()
                                }
                            } else {
                                if (state.warehouseStockyardInventoryEntriesResponse.filter { s ->
                                        (s.amount ?: 0f) > 0f
                                    }.isNotEmpty()) {
                                    checksSharedViewModel.articlesListToSelectFrom =
                                        state.warehouseStockyardInventoryEntriesResponse
                                    findNavController().navigate(R.id.checksStockyardListFragment)
                                } else {
                                    showOutOfStockBottomSheet()
                                }
                            }


                        }
                    } else {
                        if (state.isFromUserEntry) {
                            showErrorScanBottomSheet()
                        }
                    }
                }

                is DashboardViewState.ArticleResult -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    checksSharedViewModel.scannedArticle = state.articles[0]
                    handleSuccessArticleScanResultBackAction(scanType)
                }

                is DashboardViewState.HasInventories -> {
                    progressBarManager.dismiss()

                    if (!state.hasInventories) {
                        showNoInventoryBottomSheet()
                    } else {
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                InventoryActivity::class.java
                            )
                        )
                    }

                }

                is DashboardViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {
                        checksSharedViewModel.scannedStockyard = state.warehouseStockyard
                        handleSuccessStockyardScanResultBackAction(
                            state.warehouseStockyard.name,
                            state.warehouseStockyard.id,
                            scanType
                        )

                    }
                }

                is DashboardViewState.RightsResult -> {
                    state.rights?.let { userRoles ->
                        // Convert roles to modules or check them directly:
                        val modules = getUserModules(userRoles)
                        // For each base tile, decide if it's enabled:
                        val updatedTiles = dashboardList.map { tile ->
                            tile.copy(enabled = isTileEnabled(tile, modules))
                        }
                        val sortedEnabledTiles = updatedTiles
                            .filter { it.enabled }

                        dashboardAdapter.setItems(sortedEnabledTiles)
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun isTileEnabled(tile: DashBoardModel, modules: Set<ModuleType>): Boolean {
        return when (tile.title) {
            getString(R.string.incoming) -> {
                ModuleType.INCOMING in modules
            }

            getString(R.string.movements) -> {
                ModuleType.MOVEMENTS in modules
            }

            getString(R.string.packing) -> {
                ModuleType.PACKING_1 in modules
            }

            getString(R.string.inventory) -> {
                ModuleType.INVENTORY in modules
            }

            getString(R.string.defective_items) -> {
                ModuleType.DEFECTIVE_ITEMS in modules
            }

            getString(R.string.corrective_stock) -> {
                ModuleType.CORRECTIVE_STOCK in modules
            }


            else -> true // default to enabled if no check is needed
        }
    }


    private fun getUserModules(userRoles: List<String>): Set<ModuleType> {
        val result = mutableSetOf<ModuleType>()
        for (roleName in userRoles) {
            val matchingEnum = AccessRole.values().find { it.name == roleName }
            if (matchingEnum != null) {
                result.add(matchingEnum.module)
            }
        }
        return result
    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun showOutOfStockBottomSheet() {
        val bottomSheet = ItemOutOfStockBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun showNoInventoryBottomSheet() {
        val bottomSheet = NoInventoryBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun getMatchingArticlesWithNotEmptyAmount(
        articleId: String?,
        warehouseStockyardInventoryEntriesResponse: List<WarehouseStockyardInventoryEntriesResponseModel>
    ): List<WarehouseStockyardInventoryEntriesResponseModel> {
        return warehouseStockyardInventoryEntriesResponse.filter {
            it.articleId == articleId && (it.amount ?: 0f) > 0f
        }
    }


    private fun setUpAdapter() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.dashboardList.layoutManager = layoutManager
        dashboardAdapter.setInteraction(this)
        binding.dashboardList.adapter = dashboardAdapter
        dashboardAdapter.setItems(dashboardList)
    }

    private fun handelHeaderSection() {
        binding.homeHeader.headerTitle.text = getString(R.string.home_title)
        setHeaderHeight(binding.homeHeader.parentLayout)
    }

    private fun setHeaderHeight(layout: ConstraintLayout) {
        val screenHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                android.view.WindowInsets.Type.systemBars()
            )
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }

        val headerHeight = (screenHeight * 0.20).toInt()
        layout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, headerHeight)
    }


    override fun onViewClicked(item: DashBoardModel) {
        when (item.title) {
            context?.getString(R.string.incoming) -> {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        IncomingActivity::class.java
                    )
                )
            }

            context?.getString(R.string.movements) -> {
                viewModel.onEvent(DashboardEvent.GetMovements(onlyMyMovements = true))

            }

            context?.getString(R.string.packing) -> {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        PackingActivity::class.java
                    )
                )
            }

            context?.getString(R.string.inventory) -> {
                viewModel.onEvent(DashboardEvent.LoadInventory(IncomingConstants.WarehouseParam))
            }

            context?.getString(R.string.defective_items) -> {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        DefectiveItemsActivity::class.java
                    )
                )

            }

            context?.getString(R.string.corrective_stock) -> {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        CorrectingStockActivity::class.java
                    )
                )

            }

            context?.getString(R.string.checks) -> {
                openChecksBottomSheet()
            }


        }
    }

    private fun warehouseSpinnerAdapter(warehouses: List<WarehousesResponseModelItem>) {
        val adapter = WarehouseAdapter(requireContext(), warehouses)
        binding.homeHeader.warehouseSpinner.adapter = adapter

        // Load the currently saved warehouse once and set the spinner's initial selection
        lifecycleScope.launch {
            val savedWarehouse = dataStoreManager.loadWarehouse()
            val savedPosition = warehouses.indexOfFirst { it.code == savedWarehouse?.code }
            if (savedPosition != -1) {
                binding.homeHeader.warehouseSpinner.setSelection(savedPosition)
            }
        }


        // Handle spinner selection
        binding.homeHeader.warehouseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedWarehouse = warehouses[position]

                    IncomingConstants.WarehouseParam = selectedWarehouse.code

                    // Launch a coroutine to check and save the selected warehouse
                    lifecycleScope.launch {
                        val savedWarehouse = dataStoreManager.loadWarehouse()
                        if (savedWarehouse?.code != selectedWarehouse.code) {
                            dataStoreManager.saveWarehouse(selectedWarehouse)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle no selection
                }
            }
    }

    private fun openChecksBottomSheet() {
        checksAvailabilityBottomSheet = CheckAvailabilityBottomSheet.newInstance(this)
        checksAvailabilityBottomSheet.show(
            parentFragmentManager,
            "FinaliseIncompletePackingListBottomSheet"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onEvent(DashboardEvent.Reset)
    }

    override fun buttonClicked(isArticleCheck: Boolean) {
        scanType = if (isArticleCheck) ARTICLE else ONLY_STOCKYARD
        openScanBottomSheet()
        checksAvailabilityBottomSheet.dismiss()
    }


    override fun onBackClicked() {
        checksAvailabilityBottomSheet.dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onScanActionDown() {
        isScanUIButtonPressed = true
        scannerHelper?.startScanning()
    }

    override fun onScanActionUp() {
        isScanUIButtonPressed = false
        scannerHelper?.stopScanning()
    }


    private fun openScanBottomSheet() {
        scannerHelper?.stopScanning()
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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

    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.CHECKS
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
            scannerHelper = ScannerHelper(context = requireContext(),
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
        else
            scannerHelper?.startScanning()

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

    private fun updateData(data: String) {
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null
        scanPopupFragment?.dismissPopup()
        when (scanType) {
            ONLY_STOCKYARD, STOCKYARD -> {
                Toast.makeText(requireContext(), "StockYard", Toast.LENGTH_SHORT).show()
                viewModel.onEvent(DashboardEvent.GetWarehouseStockyardById(data))
            }

            ARTICLE -> {
                Toast.makeText(requireContext(), "Article", Toast.LENGTH_SHORT).show()
                viewModel.onEvent(DashboardEvent.SearchArticle(data))
            }
        }
    }

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@DashBoardFragment
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


    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            handleSuccessArticleScanResultBackAction(scanType)
        }
    }

    private fun handleSuccessArticleScanResultBackAction(scanType: ScanType) {
        checksSharedViewModel.scannedArticle?.let {
            viewModel.onEvent(
                DashboardEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = checksSharedViewModel.scannedArticle?.articleId,
                    stockyardId = "",
                    warehouseCode = IncomingConstants.WarehouseParam,
                    isFromUserEntry = true
                )
            )
        }
    }


    private fun handleSuccessStockyardScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessStockyardScan", viewLifecycleOwner
        ) { _, bundle ->
            val titleText = bundle.getString("titleText")
            val scanType = bundle.getParcelable<ScanType>("scanType")
            val stockyardId = bundle.getInt("stockyardId")
            progressBarManager.dismiss()
            handleSuccessStockyardScanResultBackAction(titleText, stockyardId, scanType)
        }
    }


    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?, stockyardById: Int?, scanType: ScanType?
    ) {
        checksSharedViewModel.scannedStockyard?.let {
            viewModel.onEvent(
                DashboardEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = "",
                    stockyardId = it.id.toString(),
                    warehouseCode = IncomingConstants.WarehouseParam,
                    isFromUserEntry = true
                )
            )
        }

    }


}