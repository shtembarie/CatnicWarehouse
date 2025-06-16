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
import com.example.catnicwarehouse.databinding.FragmentCustomersBinding
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.CustomersAdapter
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.CustomersAdapterListInteraction
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel.SearchCustomerViewModel
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
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
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
class CustomersFragment : BaseFragment(), CustomersAdapterListInteraction, ScanEventListener {

    private var _binding: FragmentCustomersBinding? = null

    private val binding get() = _binding!!

    private lateinit var customerAdapter: CustomersAdapter

    private val viewModel: SearchCustomerViewModel by viewModels()
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
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        handelHeaderSection()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSuppliersData()
        setUpAdapter()
        loadCustomers()
        handleSearch()
        handleSuccessCustomerSelectionResultBack()
        handleSuccessArticleScanResultBack()
        handleScanOptionResultBack()
    }

    private fun handleSuccessCustomerSelectionResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessCustomerSelection", viewLifecycleOwner
        ) { _, bundle ->
            val customerId = bundle.getString("customerId")
            handleActionOnCustomerSelection()
        }
    }

    private fun handleActionOnCustomerSelection() {
        val createDeliveryRequestModel = CreateDeliveryRequestModel(
            vendorId = null,
            type = sharedViewModel.getDeliveryType().toString(),
            warehouseCode = IncomingConstants.WarehouseParam,
            customerId = sharedViewModel.getSupplierInfo()?.customerId
        )
        viewModel.onEvent(SearchCustomerEvent.CreateDelivery(createDeliveryRequestModel))
    }

    private fun handleSearch() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            showSearchDialog()
        }
    }

    private fun loadCustomers(searchTerm: String? = null) {
        viewModel.onEvent(SearchCustomerEvent.SearchCustomer(searchTerm))
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

    private fun observeSuppliersData() {
        viewModel.searchedCustomersFlow.onEach { state ->
            when (state) {
                SearchCustomerViewState.Empty -> progressBarManager.dismiss()
                is SearchCustomerViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                SearchCustomerViewState.Loading -> {
                    progressBarManager.show()
                }

                SearchCustomerViewState.Reset -> progressBarManager.dismiss()
                is SearchCustomerViewState.SearchedCustomers -> {
                    progressBarManager.dismiss()
                    val customers = state.customers
                    customerAdapter.submitList(customers)
                    binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
                }

                is SearchCustomerViewState.DeliveryCreated -> {
                    progressBarManager.dismiss()
                    val deliveryId = state.createdDelivery
                    if (deliveryId != null) {
                        sharedViewModel.onEvents(
                            SharedEvent.UpdateDeliveryId(
                                deliveryId
                            )
                        )
                        openScanOptionsBottomSheet()
                    }
                }

                is SearchCustomerViewState.ArticlesForDeliveryFound -> {
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

    private fun setUpAdapter() {
        customerAdapter = CustomersAdapter(interaction = this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.supplierList.layoutManager = layoutManager
        binding.supplierList.adapter = customerAdapter
    }

    override fun onViewClicked(customer: SearchedCustomerDTOItem) {
        lifecycleScope.launch {
            sharedViewModel.onEvents(
                SharedEvent.UpdateSupplierInfo(
                    VendorOrCustomerInfo(
                        vendorId = null,
                        name = customer.company1 ?: "",
                        customerId = customer.customerId ?: ""
                    )
                )
            )
            handleActionOnCustomerSelection()
        }
    }


    private fun showSearchDialog() {
        val searchCustomersDialogFragment = SearchCustomersDialogFragment.newInstance()
        searchCustomersDialogFragment.isCancelable = false
        searchCustomersDialogFragment.show(parentFragmentManager, "searchCustomerDialog")
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
            scanEventListener = this@CustomersFragment
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
                viewModel.onEvent(SearchCustomerEvent.SearchArticle(data))
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

    override fun onScanActionDown() {
        isScanUIButtonPressed = true
        scannerHelper?.startScanning()
    }

    override fun onScanActionUp() {
        isScanUIButtonPressed = false
        scannerHelper?.stopScanning()
    }



}