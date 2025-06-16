package com.example.catnicwarehouse.incoming.suppliers.presentation.fragment

import android.os.Bundle
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
import com.example.catnicwarehouse.databinding.FragmentSupplierBinding
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel.SearchVendorViewModel
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.SupplierAdapterListInteraction
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.VendorsAdapter
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
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
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
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

@AndroidEntryPoint
class SuppliersFragment : BaseFragment(), SupplierAdapterListInteraction, ScanEventListener {

    private var _binding: FragmentSupplierBinding? = null

    private val binding get() = _binding!!

    private lateinit var vendorAdapter: VendorsAdapter

    private val viewModel: SearchVendorViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()

    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierBinding.inflate(inflater, container, false)
        handelHeaderSection()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSuppliersData()
        setUpAdapter()
        loadVendors()
        handleSearch()
        handleSuccessVendorSelectionResultBack()
        handleScanOptionResultBack()
        handleSuccessArticleScanResultBack()
    }

    private fun handleSearch() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            showSearchDialog()
        }
    }

    private fun loadVendors(searchTerm: String? = null) {
        viewModel.onEvent(SearchVendorEvent.SearchVendor(searchTerm))
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.deliveries)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handleSuccessVendorSelectionResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessVendorSelection", viewLifecycleOwner
        ) { _, bundle ->
            val vendorId = bundle.getString("vendorId")
            handleActionOnVendorSelection()
        }
    }

    private fun handleActionOnVendorSelection() {
        val createDeliveryRequestModel = CreateDeliveryRequestModel(
            vendorId = sharedViewModel.getSupplierInfo()?.vendorId,
            type = sharedViewModel.getDeliveryType().toString(),
            warehouseCode = IncomingConstants.WarehouseParam,
            customerId = null
        )
        viewModel.onEvent(SearchVendorEvent.CreateDelivery(createDeliveryRequestModel))
    }

    private fun observeSuppliersData() {
        viewModel.searchedVendorsFlow.onEach { state ->
            when (state) {
                SearchVendorViewState.Empty -> progressBarManager.dismiss()
                is SearchVendorViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                SearchVendorViewState.Loading -> {
                    progressBarManager.show()
                }

                SearchVendorViewState.Reset -> progressBarManager.dismiss()
                is SearchVendorViewState.SearchedVendors -> {
                    progressBarManager.dismiss()
                    val vendors = state.vendors?.filter { s -> s.mainVendor == true }
                    vendorAdapter.submitList(vendors)
                    binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
                }

                is SearchVendorViewState.DeliveryCreated -> {
                    progressBarManager.dismiss()
                    val deliveryId = state.data
                    if (deliveryId != null) {
                        sharedViewModel.onEvents(
                            SharedEvent.UpdateDeliveryId(
                                deliveryId
                            )
                        )
                        openScanOptionsBottomSheet()
                    }
                }

                is SearchVendorViewState.ArticlesForDeliveryFound -> {
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
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun handleSuccessArticleScanResultBackAction(scanType: ScanType) {
        if (sharedViewModel.getArticleItemModelListFromSearchedArticle().isNullOrEmpty().not()) {
            findNavController().navigate(R.id.action_supplierFragment_to_articleSelectionFragment3)
        } else {
            findNavController().navigate(R.id.action_supplierFragment_to_unloadingStockyardFragment)
        }
    }

    private fun setUpAdapter() {
        vendorAdapter = VendorsAdapter(interaction = this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.supplierList.layoutManager = layoutManager
        binding.supplierList.adapter = vendorAdapter
    }

    override fun onViewClicked(supplier: SearchedVendorDTO) {
        lifecycleScope.launch {
            sharedViewModel.onEvents(
                SharedEvent.UpdateSupplierInfo(
                    VendorOrCustomerInfo(
                        vendorId = supplier.vendorId,
                        name = supplier.company1 ?: "",
                        customerId = null
                    )
                )
            )
            handleActionOnVendorSelection()
        }
    }


    private fun showSearchDialog() {
        val searchSuppliersDialogFragment = SearchSuppliersDialogFragment.newInstance()
        searchSuppliersDialogFragment.isCancelable = false
        searchSuppliersDialogFragment.show(parentFragmentManager, "searchDialog")
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

    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            handleSuccessArticleScanResultBackAction(scanType)
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

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@SuppliersFragment
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

            }

            ARTICLE -> {
                viewModel.onEvent(SearchVendorEvent.SearchArticle(data))
            }
        }
    }

    private fun openScanOptionsBottomSheet() {
        scannerHelper?.stopScanning()
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun handleArgsUpdateFromMatchFoundResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "UpdateArgsFromMatchFoundFragment", viewLifecycleOwner
        ) { _, bundle ->
            scanType = bundle.getParcelable<ScanType>("scanType")!!
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

    override fun onScanActionDown() {
        isScanUIButtonPressed = true
        scannerHelper?.startScanning()
    }

    override fun onScanActionUp() {
        isScanUIButtonPressed = false
        scannerHelper?.stopScanning()
    }


}