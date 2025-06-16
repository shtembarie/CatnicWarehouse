package com.example.catnicwarehouse.movement.matchFound.presentation.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchFoundBinding
import com.example.catnicwarehouse.movement.matchFound.presentation.bottomSheet.PickUpConfirmationBottomSheet
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundEvent
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundViewState
import com.example.catnicwarehouse.movement.matchFound.presentation.viewModel.MatchFoundViewModel
import com.example.catnicwarehouse.movement.shared.MovementActionType
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
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
import com.example.shared.repository.movements.PickUpRequestModel
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
class FragmentMatchFound : BaseFragment(), ScanEventListener {

    private var _binding: FragmentMatchFoundBinding? = null
    private val binding get() = _binding!!
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val viewModel: MatchFoundViewModel by viewModels()

    private var scanType: ScanType = STOCKYARD
    private var scannerHelper: ScannerHelper? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
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
        _binding = FragmentMatchFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        handleData()
        handlePickUpAndDropOffButtonAction()
        observeMatchFoundEvents()
        handleScanOptionResultBack()
        handleSuccessStockyardScanResultBack()
    }


    private fun observeMatchFoundEvents() {
        viewModel.matchFoundFlow.onEach { state ->
            when (state) {
                MatchFoundViewState.Empty -> progressBarManager.dismiss()
                is MatchFoundViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }


                MatchFoundViewState.Loading -> progressBarManager.show()
                MatchFoundViewState.Reset -> progressBarManager.dismiss()
                is MatchFoundViewState.PickUpResult -> {
                    progressBarManager.dismiss()
                    navigateToMovementsListFragment()
                }

                is MatchFoundViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {

                        val action =
                            FragmentMatchFoundDirections.actionMatchFoundFragment3ToStockyardTreeFragment2()
                        action.selectedStockyardId = state.warehouseStockyard.id.toString()
                        action.scanType = scanType.type
                        action.moduleType = ModuleType.MOVEMENTS.type
                        findNavController().navigate(action)

                    }
                }

                is MatchFoundViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty())
                        return@onEach

                    movementSharedViewModel.selectedWarehouseStockyardInventoryEntry =
                        state.warehouseStockyardInventoryEntriesResponse[0]
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToMovementsListFragment() {
        val action = FragmentMatchFoundDirections.actionMatchFoundFragment3ToMovementsListFragment()
        findNavController().popBackStack(R.id.movementsListFragment, inclusive = false)
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.header_match_found)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handleData() {
        with(binding) {
            //id section
            //match code field is missing from the api response model
            val matchCode = movementSharedViewModel.selectedArticle?.stockYardName?:movementSharedViewModel.currentMovementItemToDropOff?.sourceWarehouseStockYardName
            idSection.titleId.text = matchCode
            if (matchCode.isNullOrEmpty().not())
                idSection.decsriptionId.visibility = View.VISIBLE
            else
                idSection.decsriptionId.visibility = View.GONE
            idSection.decsriptionId.text = getString(R.string.stockyard_name)
            idSection.toRightBtn.visibility = View.GONE

            //article number

            val articleId = movementSharedViewModel.selectedArticle?.articleId?:movementSharedViewModel.currentMovementItemToDropOff?.articleId
            articleSection.titleId.text = articleId
            articleSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.hash_img)
            )
            articleSection.decsriptionId.visibility = View.VISIBLE
            articleSection.decsriptionId.text = getString(R.string.article_number)
            articleSection.toRightBtn.visibility = View.GONE

            //Available to move section
            val availableAmount =
                (movementSharedViewModel.selectedArticle?.amount?:movementSharedViewModel.currentMovementItemToDropOff?.amount)?.toInt().toString()
            quantitySection.titleId.text = availableAmount
            quantitySection.decsriptionId.visibility = View.VISIBLE
            quantitySection.decsriptionId.text = getString(R.string.available_to_move)
            quantitySection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.avalaible_to_download)
            )
            quantitySection.toRightBtn.visibility = View.GONE

            //picked up/ dropped off section
            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
                val amountToPickUp = (movementSharedViewModel.selectedArticle?.amountTakenForPickUp)?.toInt()
                defectiveSection.titleId.text = (amountToPickUp ?: 0).toString()
                defectiveSection.decsriptionId.visibility = View.VISIBLE
                defectiveSection.decsriptionId.text = getString(R.string.amount_to_pick_up)
                defectiveSection.imgProduct.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.amount_to_pick_up
                    )
                )
            } else if (movementSharedViewModel.movementActionType == MovementActionType.DROP_OFF) {
                val amountToPickUp =
                    (movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff)?.toInt()
                defectiveSection.titleId.text = (amountToPickUp ?: 0).toString()
                defectiveSection.decsriptionId.visibility = View.VISIBLE
                defectiveSection.decsriptionId.text = getString(R.string.amount_to_drop_off)
                defectiveSection.imgProduct.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.amount_to_drop_off_icon
                    )
                )
            }

            putInStockyardButton.visibility = View.GONE
            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP)
                confirmButton.text = getString(R.string.pick_up)
            else
                confirmButton.text = getString(R.string.drop_off)
            handleAmountButtonAction()

        }
    }

    private fun handleAmountButtonAction() {
        binding.defectiveSection.viewContainer.setOnClickListener {
            val action =
                FragmentMatchFoundDirections.actionMatchFoundFragment3ToAmountFragment()
            findNavController().navigate(action)
        }
    }


    private fun showPickUpConfirmationBottomSheet() {
        val bottomSheet = PickUpConfirmationBottomSheet {
            val movementId = movementSharedViewModel.currentMovement?.id.toString()
            val pickUpRequestModel = PickUpRequestModel(
                articleId =  movementSharedViewModel.selectedArticle?.articleId,
                unitCode = movementSharedViewModel.selectedArticle?.unitCode,
                amount =  (movementSharedViewModel.selectedArticle?.amountTakenForPickUp)?.toInt(),
                inventoryEntryId = movementSharedViewModel.selectedArticle?.id
            )
            viewModel.onEvent(MatchFoundEvent.PickUp(movementId, pickUpRequestModel))
        }
        bottomSheet.show(childFragmentManager, "PickUpConfirmationBottomSheet")
    }

    private fun handlePickUpAndDropOffButtonAction() {
        binding.confirmButton.setOnClickListener {
            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
                if(movementSharedViewModel.selectedArticle?.amountTakenForPickUp==null || movementSharedViewModel.selectedArticle?.amountTakenForPickUp == 0f){
                    showErrorBanner(getString(R.string.pick_up_amount_cannot_be_0))
                    return@setOnClickListener
                }
                if((movementSharedViewModel.selectedArticle?.amountTakenForPickUp ?: 0f) > (movementSharedViewModel.selectedArticle?.amount ?: 0f)
                ){
                    showErrorBanner(getString(R.string.cannot_pick_up_more_than_available_items))
                    return@setOnClickListener
                }
                showPickUpConfirmationBottomSheet()
            } else if (movementSharedViewModel.movementActionType == MovementActionType.DROP_OFF)  {
                if(movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff==null || movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff == 0f){
                    showErrorBanner(getString(R.string.drop_off_amount_cannot_be_0))
                    return@setOnClickListener
                }
                if((movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff ?: 0f) > (movementSharedViewModel.currentMovementItemToDropOff?.amount ?: 0)
                ){
                    showErrorBanner(getString(R.string.cannot_drop_off_more_than_available_items))
                    return@setOnClickListener
                }
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

        }
        binding.confirmButton.setOnClickListener {
            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
                if(movementSharedViewModel.selectedArticle?.amountTakenForPickUp==null || movementSharedViewModel.selectedArticle?.amountTakenForPickUp == 0f){
                    showErrorBanner(getString(R.string.pick_up_amount_cannot_be_0))
                    return@setOnClickListener
                }
                if((movementSharedViewModel.selectedArticle?.amountTakenForPickUp ?: 0f) > (movementSharedViewModel.selectedArticle?.amount ?: 0f)
                ){
                    showErrorBanner(getString(R.string.cannot_pick_up_more_than_available_items))
                    return@setOnClickListener
                }
                showPickUpConfirmationBottomSheet()
            } else if (movementSharedViewModel.movementActionType == MovementActionType.DROP_OFF)  {
                if(movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff==null || movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff == 0f){
                    showErrorBanner(getString(R.string.drop_off_amount_cannot_be_0))
                    return@setOnClickListener
                }
                if((movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff ?: 0f) > (movementSharedViewModel.currentMovementItemToDropOff?.amount ?: 0)
                ){
                    showErrorBanner(getString(R.string.cannot_drop_off_more_than_available_items))
                    return@setOnClickListener
                }
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
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
        if (scannerHelper == null)
            scannerHelper = ScannerHelper(context = requireContext(),
                scannerType = scannerType,
                updateStatus = { status,scannerState ->
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
            scanEventListener = this@FragmentMatchFound
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
                    MatchFoundEvent.GetWarehouseStockyardById(
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
            moduleType = ModuleType.MOVEMENTS
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
            handleSuccessStockyardScanResultBackAction(titleText,stockyardId, scanType)
        }
    }

    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        stockyardId: Int,
        scanType: ScanType?
    ) {

        if (scanType == STOCKYARD) {
            titleText?.let {
                val bottomSheet = SuccessScanBottomSheet.newInstance(
                    titleText = it,
                    isIconVisible = false,
                    descriptionText = "",
                    button1Text = getString(R.string.yes_drop_off),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        movementSharedViewModel.scannedStockyard?.let {
                            movementSharedViewModel.currentMovementItemToDropOff?.destinationWarehouseStockYardName = it.name
                            movementSharedViewModel.currentMovementItemToDropOff?.destinationWarehouseStockYardId = it.id
                            navigateToSummaryScreen()
                        }
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

    private fun navigateToSummaryScreen() {
        val action = FragmentMatchFoundDirections.actionMatchFoundFragment3ToMovementSummaryFragment()
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null

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