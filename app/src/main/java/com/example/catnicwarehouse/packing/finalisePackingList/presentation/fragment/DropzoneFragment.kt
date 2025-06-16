package com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDropzoneBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.DefaultPackingZoneAdapterInteraction
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.DefaultPackingZonesAdapter
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.viewModel.FinalisePackingViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum.BARCODE
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum.CAMERA
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum.MANUAL
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
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
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
class DropzoneFragment : BaseFragment(), DefaultPackingZoneAdapterInteraction, ScanEventListener {

    private var _binding: FragmentDropzoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinalisePackingViewModel by viewModels()
    private lateinit var defaultPackingZonesAdapter: DefaultPackingZonesAdapter
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = STOCKYARD
    private var scannerHelper: ScannerHelper? = null
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private var defaultPackingZoneList: List<DefaultPackingZoneResultModel>? = null
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
        _binding = FragmentDropzoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        viewModel.onEvent(FinalisePackingEvent.GetDefaultPackingZones)
        setUpAdapter()
        observeDefaultPackingZonesEvent()

        handleScanStockyardButtonAction()

        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from stockyard scan success
        handleSuccessStockyardScanResultBack()
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.select_the_dropzone)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handleScanStockyardButtonAction() {
        binding.scanPositionButton.setOnClickListener {
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.scanPositionTextView.setOnClickListener {
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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
        titleText: String?,
        scanType: ScanType?,
        stockyardId: Int?
    ) {
        val matchingStockyardInventoryEntry = getMatchingStockyards(stockyardId)
        if (matchingStockyardInventoryEntry.isNullOrEmpty()) {
            showErrorScanBottomSheet()
            return
        }

        packingSharedViewModel.dropzoneScannedStockyardId = matchingStockyardInventoryEntry[0].id

        if (scanType == STOCKYARD) {
            titleText?.let {
                val bottomSheet = SuccessScanBottomSheet.newInstance(
                    titleText = it,
                    isIconVisible = false,
                    descriptionText = getString(R.string.match_code_found_would_like_to_continue_dropping_off_articles),
                    button1Text = getString(R.string.yes_drop_off),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        navigateToFinaliseFragment(packingSharedViewModel.dropzoneScannedStockyardId)
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

    private fun navigateToFinaliseFragment(dropzoneId: Int?) {
        parentFragmentManager.setFragmentResult(
            "handleSuccessDropzoneScan",
            bundleOf(
                "dropzoneId" to dropzoneId
            )
        )
        findNavController().popBackStack(R.id.finalisePackingFragment, false)
    }


    private fun getMatchingStockyards(stockyardId: Int?): List<DefaultPackingZoneResultModel>? {
        val listToMatchFrom = defaultPackingZoneList
        return listToMatchFrom?.filter { s -> s.id == stockyardId }

    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun observeDefaultPackingZonesEvent() {
        viewModel.finalisePackingFlow.onEach { state ->
            when (state) {

                FinalisePackingViewState.Empty -> progressBarManager.dismiss()
                is FinalisePackingViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is FinalisePackingViewState.GetDefaultPackingZonesResult -> {
                    progressBarManager.dismiss()
                    defaultPackingZoneList = state.defaultPackingZones
                    defaultPackingZonesAdapter.submitList(state.defaultPackingZones)
                }

                FinalisePackingViewState.Loading -> progressBarManager.show()

                FinalisePackingViewState.Reset -> progressBarManager.dismiss()
                else -> {
                    progressBarManager.dismiss()
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setUpAdapter() {
        defaultPackingZonesAdapter =
            DefaultPackingZonesAdapter(interaction = this, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = defaultPackingZonesAdapter

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
            moduleType = ModuleType.PACKING_2
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

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@DropzoneFragment
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
                viewModel.onEvent(FinalisePackingEvent.SearchArticle(data))
            }
        }
    }

    override fun onViewClicked(data: DefaultPackingZoneResultModel) {
        packingSharedViewModel.dropzoneScannedStockyardId = data.id
        navigateToFinaliseFragment(packingSharedViewModel.dropzoneScannedStockyardId)
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