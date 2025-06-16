package com.example.catnicwarehouse.packing.stockyards.presentation.fragment


import android.os.Bundle
import android.util.Log
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
import com.example.catnicwarehouse.databinding.AvailableStockyardsFragmentBinding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.stockyards.presentation.adapter.StockyardsAdapter
import com.example.catnicwarehouse.packing.stockyards.presentation.adapter.WarehouseStockyardsAdapterInteraction
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.packing.stockyards.presentation.sealedClasses.StockyardEvent
import com.example.catnicwarehouse.packing.stockyards.presentation.sealedClasses.StockyardsViewState
import com.example.catnicwarehouse.packing.stockyards.presentation.viewModel.StockyardsViewModel
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

@AndroidEntryPoint
class AvailableStockyardsFragment : BaseFragment(), WarehouseStockyardsAdapterInteraction,
    ScanEventListener {

    private var _binding: AvailableStockyardsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StockyardsViewModel by viewModels()
    private lateinit var stockyardsAdapter: StockyardsAdapter

    private var scanType: ScanType = STOCKYARD
    private var scannerHelper: ScannerHelper? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
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
        _binding = AvailableStockyardsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        observeEvents()
        handleScanStockyardButtonAction()

        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from stockyard scan success
        handleSuccessStockyardScanResultBack()

        stockyardsAdapter.submitList(sortAvailableInList(packingSharedViewModel.stockyardsListToSelectFrom?.distinctBy { s -> s.stockYardId }
            ?.filter { s -> s.warehouseCode == IncomingConstants.WarehouseParam }))
    }

    private fun setUpAdapter() {
        stockyardsAdapter = StockyardsAdapter(interaction = this, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.stockyardsList.layoutManager = layoutManager
        binding.stockyardsList.adapter = stockyardsAdapter
    }

    private fun sortAvailableInList(stockyardsListToSelectFrom: List<WarehouseStockyardInventoryEntriesResponseModel>?): List<WarehouseStockyardInventoryEntriesResponseModel>? {
        Log.d("stockyardsListToSelectFrom1", stockyardsListToSelectFrom.toString())
        val sortedList = stockyardsListToSelectFrom?.sortedWith(
            compareByDescending<WarehouseStockyardInventoryEntriesResponseModel> { it.isConnectedArticle == true }
                .thenByDescending { it.isConnectedArticle == false }
                .thenByDescending { it.isConnectedArticle == null }
        )
        Log.d("stockyardsListToSelectFrom2", sortedList.toString())
        return sortedList ?: stockyardsListToSelectFrom
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.available_in)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@AvailableStockyardsFragment
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


    private fun observeEvents() {
        viewModel.stockyardsFlow.onEach { state ->
            when (state) {
                StockyardsViewState.Empty -> progressBarManager.dismiss()
                is StockyardsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                StockyardsViewState.Loading -> progressBarManager.show()
                StockyardsViewState.Reset -> progressBarManager.dismiss()
                is StockyardsViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {

                        val action =
                            AvailableStockyardsFragmentDirections.actionAvailableStockyardsFragmentToStockyardTreeFragment3()
                        action.selectedStockyardId = state.warehouseStockyard.id.toString()
                        action.scanType = scanType.type
                        action.moduleType = ModuleType.PACKING_1.type
                        findNavController().navigate(action)

                    }
                }

                else -> {
                    progressBarManager.dismiss()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun navigateToAmountFragment() {
        val action =
            AvailableStockyardsFragmentDirections.actionAvailableStockyardsFragmentToAmountFragment2()
        findNavController().navigate(action)
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
                        data
                    )
                )
            }

            ARTICLE -> {

            }
        }
    }

    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.PACKING_1
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
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
            val topParentId = bundle.getInt("parentStockYardId")
            handleSuccessStockyardScanResultBackAction(titleText, scanType, stockyardId)
        }
    }

    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        scanType: ScanType?,
        stockyardId: Int?,
    ) {
        val matchingStockyardInventoryEntry = getMatchingStockyards(stockyardId)
        if (matchingStockyardInventoryEntry.isNullOrEmpty()) {
            showErrorScanBottomSheet()
            return
        }

        packingSharedViewModel.selectedStockyardIdInventoryEntry =
            matchingStockyardInventoryEntry[0]

        if (scanType == STOCKYARD) {
            titleText?.let {
                val bottomSheet = SuccessScanBottomSheet.newInstance(
                    titleText = getString(R.string.match_location, it),
                    isIconVisible = false,
                    descriptionText = getString(R.string.would_like_to_continue_packing_articles_fom_here),
                    button1Text = getString(R.string.continue_string),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        navigateToAmountFragment()
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


    private fun getMatchingStockyards(stockyardId: Int?): List<WarehouseStockyardInventoryEntriesResponseModel>? {
        val listToMatchFrom = packingSharedViewModel.stockyardsListToSelectFrom
        return listToMatchFrom?.filter { s -> s.stockYardId == stockyardId }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        viewModel.onEvent(StockyardEvent.Reset)

    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    override fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel) {
        packingSharedViewModel.selectedStockyardIdInventoryEntry = data
        navigateToAmountFragment()
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