package com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentUnloadingStockyardsBinding
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.enums.ScanType.*
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.adapter.UnloadingStockyardsAdapter
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.adapter.UnloadingStockyardsAdapterInteraction
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.viewModel.UnloadingStockyardsViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum.*
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
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
class UnloadingStockyardsFragment : BaseFragment(), UnloadingStockyardsAdapterInteraction,
    ScanEventListener {

    private var _binding: FragmentUnloadingStockyardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UnloadingStockyardsViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private lateinit var unloadingStockyardAdapter: UnloadingStockyardsAdapter
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ONLY_STOCKYARD
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false

    private val args: UnloadingStockyardsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnloadingStockyardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.scanType?.let {
            scanType = ScanType.valueOf(it)
        }

        handleButtonActions()
        handleHeaderSection()
        setUpAdapter()
        observeSharedEvents()
        viewModel.onEvent(UnloadingStockyardsEvent.FindDefaultPickUpAndDropZones(IncomingConstants.WarehouseParam))
        observeUnloadingStockyards()


        //handle the result back from stockyard scan success
        handleSuccessStockyardScanResultBack()
        //handle the result back from article scan success
        handleSuccessArticleScanResultBack()
        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from MatchFoundFragment
        handleArgsUpdateFromMatchFoundResultBack()
    }

    private fun handleSuccessStockyardScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessStockyardScan", viewLifecycleOwner
        ) { _, bundle ->
            val titleText = bundle.getString("titleText")
            val scanType = bundle.getParcelable<ScanType>("scanType")
            val stockyardId = bundle.getInt("stockyardId")

            handleSuccessStockyardScanResultBackAction(titleText, stockyardId, scanType)
        }
    }


    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        stockyardId: Int,
        scanType: ScanType?
    ) {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedWarehouseStockyard(
                stockyardId
            ),
            SharedEvent.UpdateSelectedWarehouseStockyardName(
                selectedWarehouseStockyardName = titleText?:""
            )

        )

//        if (scanType == STOCKYARD) {
//            titleText?.let {
//                val bottomSheet = SuccessScanBottomSheet.newInstance(
//                    titleText = it,
//                    isIconVisible = false,
//                    descriptionText = getString(R.string.match_code_found_would_like_to_continue),
//                    button1Text = getString(R.string.yes_scan_articles),
//                    button2Text = getString(R.string.scan_again),
//                    button1Callback = {
//                        scannerHelper?.stopScanning()
//                        // Show the ScanOptionsBottomSheet for ARTICLE scan type
//                        val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
//                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//                    },
//                    button2Callback = {
//                        scannerHelper?.stopScanning()
//                        // Show the ScanOptionsBottomSheet for STOCKYARD scan type
//                        val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ONLY_STOCKYARD)
//                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//                    }
//                )
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//            }
//        } else
//            if (scanType == ONLY_STOCKYARD) {
            findNavController().navigate(R.id.action_unloadingStockyardFragment_to_matchFoundFragment)
//        }
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
                updateStatus = { status,scannerState ->
                    updateStatus(
                        status = status,
                        scannerStates=scannerState
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

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@UnloadingStockyardsFragment
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
                    UnloadingStockyardsEvent.GetWarehouseStockyardById(
                        data
                    )
                )
            }

            ARTICLE -> {
                viewModel.onEvent(UnloadingStockyardsEvent.SearchArticle(data))
            }
        }
    }

    private fun handleArgsUpdateFromMatchFoundResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "UpdateArgsFromMatchFoundFragment", viewLifecycleOwner
        ) { _, bundle ->
            scanType = bundle.getParcelable<ScanType>("scanType")!!
        }
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
        if (sharedViewModel.getArticleItemModelListFromSearchedArticle().isNullOrEmpty().not()) {
            findNavController().navigate(R.id.action_unloadingStockyardFragment_to_articleSelectionFragment3)
        } else {
            findNavController().navigate(R.id.action_unloadingStockyardFragment_to_matchFoundFragment)
        }
    }

    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.INCOMING
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
    }

    private fun observeSharedEvents() {
        sharedViewModel.incomingSharedFlow.onEach { state ->
            when (state) {
                IncomingSharedViewState.Empty -> progressBarManager.dismiss()
                is IncomingSharedViewState.Error -> progressBarManager.dismiss()
                IncomingSharedViewState.Loading -> progressBarManager.show()
                is IncomingSharedViewState.SelectedWarehouseStockyardIdResult -> progressBarManager.dismiss()
                is IncomingSharedViewState.SelectedArticleItemModelResult -> progressBarManager.dismiss()
                is IncomingSharedViewState.SelectedWarehouseStockyardNameResult -> progressBarManager.dismiss()
                else -> {
                    progressBarManager.dismiss()
                    handleButtonActions()
                    handleHeaderSection()
                    setUpAdapter()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.unloading_stockyards)
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

    private fun handleButtonActions() {
        with(binding) {
            scanStockyardsButton.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            scanStockyardsTextView.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            continueWithoutButton.setOnClickListener {

                if (scanType == ONLY_STOCKYARD) {
                    findNavController().navigate(R.id.action_unloadingStockyardFragment_to_matchFoundFragment)
                } else {
                    sharedViewModel.onEvents(
                        SharedEvent.UpdateSelectedWarehouseStockyard(
                            selectedWarehouseStockyard = 0
                        )
                    )
                    scannerHelper?.stopScanning()
                    val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = ARTICLE)
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                }
            }
        }
    }

    private fun setUpAdapter() {
        unloadingStockyardAdapter = UnloadingStockyardsAdapter(interaction = this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.stockyardsList.layoutManager = layoutManager
        binding.stockyardsList.adapter = unloadingStockyardAdapter
    }


    private fun observeUnloadingStockyards() {
        viewModel.unloadingStockyardsFlow.onEach { state ->
            when (state) {
                is UnloadingStockyardsViewState.DefaultPickUpAndDropZonesFound -> {
                    progressBarManager.dismiss()
                    unloadingStockyardAdapter.submitList(state.stockyards?.filter { s -> s.defaultPickAndDropZone })
                }

                UnloadingStockyardsViewState.Empty -> progressBarManager.dismiss()
                is UnloadingStockyardsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                UnloadingStockyardsViewState.Loading -> progressBarManager.show()
                UnloadingStockyardsViewState.Reset -> progressBarManager.dismiss()
                is UnloadingStockyardsViewState.WarehousesFound -> {
                    progressBarManager.dismiss()
                }

                is UnloadingStockyardsViewState.ArticlesForDeliveryFound -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    sharedViewModel.onEvents(
                        SharedEvent.UpdateSelectedArticleItemModel(
                            SharedEvent.mapArticleItemForDeliveryToArticleItemUI(
                                state.articles[0]
                            )
                        ),
                        SharedEvent.UpdateSelectedQty(state.articles[0].quantityInPurchaseOrders.toString())
                    )

                    progressBarManager.dismiss()
                    handleSuccessArticleScanResultBackAction(scanType)
                }

                is UnloadingStockyardsViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {

                        val action =
                            UnloadingStockyardsFragmentDirections.actionUnloadingStockyardFragmentToStockyardTreeFragment()
                        action.selectedStockyardId = state.warehouseStockyard.id.toString()
                        action.scanType = scanType.type
                        findNavController().navigate(action)


                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onViewClicked(warehouseStockyardsDTO: WarehouseStockyardsDTO) {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedWarehouseStockyard(
                selectedWarehouseStockyard = warehouseStockyardsDTO.id
            ),
            SharedEvent.UpdateSelectedWarehouseStockyardName(
                selectedWarehouseStockyardName = warehouseStockyardsDTO.name
            )
        )

        when (scanType) {
            ONLY_STOCKYARD -> {
                findNavController().navigate(R.id.action_unloadingStockyardFragment_to_matchFoundFragment)
            }

            else -> {
                findNavController().navigate(R.id.action_unloadingStockyardFragment_to_matchFoundFragment)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()

    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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