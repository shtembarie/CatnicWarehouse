package com.example.catnicwarehouse.movement.movementList.presentation.fragment


import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMovementsListBinding
import com.example.catnicwarehouse.movement.articles.presentation.fragment.MovementArticlesFragmentDirections
import com.example.catnicwarehouse.movement.movementList.presentation.activity.MovementsActivity
import com.example.catnicwarehouse.movement.movementList.presentation.adapter.MovementsAdapter
import com.example.catnicwarehouse.movement.movementList.presentation.adapter.MovementsAdapterInteraction
import com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses.MovementsEvent
import com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses.MovementsViewState
import com.example.catnicwarehouse.movement.movementList.presentation.viewModel.MovementsViewModel
import com.example.catnicwarehouse.movement.shared.MovementActionType
import com.example.catnicwarehouse.movement.shared.MovementStatus
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
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
import com.example.catnicwarehouse.tools.popup.showCloseMovementConfirmationPopup
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.movements.MovementItem
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
class MovementsListFragment : BaseFragment(), MovementsAdapterInteraction, ScanEventListener {


    private var _binding: FragmentMovementsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovementsViewModel by viewModels()
    private val movementsSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private lateinit var movementsAdapter: MovementsAdapter
    private var clickedMovementItem: MovementItem? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movementsSharedViewModel.initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovementsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        handelHeaderSection()
        updateButtonsUI(hasItems = false)
        viewModel.onEvent(MovementsEvent.GetMovements(onlyMyMovements = true))
        observeEvents()
        handlePickupButtonAction()
        handleDropOffButtonAction()
        handleScanOptionResultBack()
        //handle the result back from article scan success
        handleSuccessArticleScanResultBack()
        handleBannerLogicForDroppedOffItems()
        handleCloseMenuAction()
    }

    private fun handleDropOffButtonAction() {
        binding.dropOffButton.setOnClickListener {
            movementsSharedViewModel.movementActionType = MovementActionType.DROP_OFF
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.dropOffTextView.setOnClickListener {
            movementsSharedViewModel.movementActionType = MovementActionType.DROP_OFF
            scannerHelper?.stopScanning()
            val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun handlePickupButtonAction() {
        binding.pickUpButton.setOnClickListener {
            movementsSharedViewModel.movementActionType = MovementActionType.PICK_UP
            val action =
                MovementsListFragmentDirections.actionMovementsListFragmentToWarehouseStockyardsFragment()
            findNavController().navigate(action)
        }
    }

    private fun handleCloseMenuAction() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            showPopupMenu(binding.deliveryHeader.rightToolbarButton)
        }
    }


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.movements_list)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            (requireActivity() as MovementsActivity).finish()
        }
    }

    private fun setUpAdapter() {
        movementsAdapter = MovementsAdapter(interaction = this, showArrow = true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.movementsList.layoutManager = layoutManager
        binding.movementsList.adapter = movementsAdapter
    }

    private fun observeEvents() {
        viewModel.movementsFlow.onEach { state ->
            when (state) {
                MovementsViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is MovementsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is MovementsViewState.GetMovementsResult -> {
                    progressBarManager.dismiss()
                    movementsSharedViewModel.movementItemsList =
                        state.movements?.filter { s -> s.status == MovementStatus.OPEN.name }
                            ?.get(0)?.movementItems?.filter { s -> s.movementOpen }
                            ?.let { ArrayList(it) }
                    movementsSharedViewModel.currentMovement =
                        state.movements?.filter { s -> s.status == MovementStatus.OPEN.name }
                            ?.get(0)
                    if (movementsSharedViewModel.movementItemsList.isNullOrEmpty().not()) {
                        //show the movements list
                        binding.emptyLayout.visibility = View.GONE
                        binding.movementsList.visibility = View.VISIBLE
                        movementsAdapter.submitList(movementsSharedViewModel.movementItemsList)
                        updateButtonsUI(hasItems = true)
                    }
                    handleMenuUI()
                }

                MovementsViewState.Loading -> progressBarManager.show()
                MovementsViewState.Reset -> progressBarManager.dismiss()
                is MovementsViewState.ArticleResult -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    movementsSharedViewModel.scannedArticle = state.articles[0]
                    handleSuccessArticleScanResultBackAction(scanType)
                }

                is MovementsViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()


                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (state.isFromUserEntry) {
                            // In case only 1 item is matched show confirmation and move to MatchFound screen
                            // else inform user about multiple articles with same id and move to article selection screen
                            val movementItems =
                                getMovementItemsForScannedArticle(state.warehouseStockyardInventoryEntriesResponse)
                            if (movementItems.isEmpty()) {
                                showErrorScanBottomSheet()
                                return@onEach
                            }
                            if (movementItems.count() == 1) {

                                //Check if a movement item exists with the selected article
                                // else show error to the user
                                movementsSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                        button1Text = getString(R.string.yes_select_item),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            movementsSharedViewModel.currentMovementItemToDropOff =
                                                movementItems[0]
                                            movementsSharedViewModel.scannedArticle = null
                                            if (movementsSharedViewModel.movementActionType == MovementActionType.DROP_OFF)
                                                movementsSharedViewModel.selectedArticle = null
                                            val action =
                                                MovementsListFragmentDirections.actionMovementsListFragmentToMatchFoundFragment3()
                                            findNavController().navigate(action)

                                        },
                                        button2Callback = {
                                            movementsSharedViewModel.scannedArticle = null
                                            movementsSharedViewModel.currentMovementItemToDropOff =
                                                null
                                            scannerHelper?.stopScanning()
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ARTICLE)
                                            bottomSheet.show(
                                                parentFragmentManager,
                                                bottomSheet.tag
                                            )
                                        })
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            } else {
                                movementsSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                                        button1Text = getString(R.string.yes_continue),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            movementsSharedViewModel.movementItemsListToSelectFrom =
                                                movementItems
                                            val action =
                                                MovementsListFragmentDirections.actionMovementsListFragmentToMovementSelectionFragment()
                                            findNavController().navigate(action)

                                        },
                                        button2Callback = {
                                            scannerHelper?.stopScanning()
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ARTICLE)
                                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                        })
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            }


                        } else {

                            movementsSharedViewModel.selectedArticle =
                                state.warehouseStockyardInventoryEntriesResponse.filter { s -> s.articleId == clickedMovementItem?.articleId }?.get(0)
                            movementsSharedViewModel.selectedArticle?.let {
                                val action =
                                    MovementsListFragmentDirections.actionMovementsListFragmentToMatchFoundFragment3()
                                findNavController().navigate(action)
                            }
                        }
                    } else {
                        if (state.isFromUserEntry) {
                            showErrorScanBottomSheet()
                        } else {
                            showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
                        }
                    }
                }

                is MovementsViewState.CloseMovementResult -> {
                    progressBarManager.dismiss()
                    requireActivity().finish()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleMenuUI() {
        binding.deliveryHeader.rightToolbarButton.isVisible =
            movementsSharedViewModel.movementItemsList.isNullOrEmpty()
        binding.deliveryHeader.rightToolbarButton.setImageDrawable(requireContext().getDrawable(R.drawable.menu_icon))
    }


    @SuppressLint("Recycle")
    private fun updateButtonsUI(hasItems: Boolean) {
        if (!hasItems) {
            with(binding) {
                val themedContext =
                    ContextThemeWrapper(requireContext(), R.style.OutlineDisabledButton)
                dropOffButton.setBackgroundResource(
                    themedContext.obtainStyledAttributes(
                        R.style.OutlineDisabledButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )
                dropOffIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey_400
                    )
                )
                dropOffTextView.setTextColor(requireContext().getColor(R.color.grey_400))
                dropOffButton.isEnabled = false
            }
        } else {
            with(binding) {
                val themedContext = ContextThemeWrapper(requireContext(), R.style.OutlineButton)
                dropOffButton.setBackgroundResource(
                    themedContext.obtainStyledAttributes(
                        R.style.OutlineButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )
                dropOffIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blood_orange
                    )
                )
                dropOffTextView.setTextColor(requireContext().getColor(R.color.blood_orange))
                dropOffButton.isEnabled = true
            }
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
            scanEventListener = this@MovementsListFragment
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
                viewModel.onEvent(MovementsEvent.SearchArticle(data))
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

    private fun handleSuccessArticleScanResultBackAction(scanType: ScanType) {
        movementsSharedViewModel.scannedArticle?.let {
            viewModel.onEvent(
                MovementsEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = movementsSharedViewModel.scannedArticle?.articleId,
                    stockyardId = (movementsSharedViewModel.scannedStockyard?.id ?: "").toString(),
                    warehouseCode = movementsSharedViewModel.scannedStockyard?.warehouseCode,
                    isFromUserEntry = true
                )
            )
        }
    }

    private fun getMovementItemsForScannedArticle(dataList: List<WarehouseStockyardInventoryEntriesResponseModel>): List<MovementItem> {
        return dataList.flatMap { dataItem ->
            movementsSharedViewModel.movementItemsList?.filter { movementItem ->
                movementItem.articleId == dataItem.articleId &&
                        movementItem.sourceWarehouseStockYardId == dataItem.stockYardId &&
                        movementItem.sourceWarehouseStockYardInventoryEntryId == dataItem.id
            } ?: emptyList() // Return an empty list if movementItemsList is null
        }
    }

    private fun handleBannerLogicForDroppedOffItems() {
        if (movementsSharedViewModel.itemsDropped != 0) {
            showPositiveBanner("${movementsSharedViewModel.itemsDropped} items were dropped off.")
            movementsSharedViewModel.itemsDropped = 0
        }
    }

    private fun showPopupMenu(view: View) {
        // Create a PopupMenu, linking it to the button (view)
        val popup = PopupMenu(requireContext(), view)

        // Inflate the popup with your menu resource
        popup.menuInflater.inflate(R.menu.movement_menu, popup.menu)

        // Set a listener to handle menu item clicks
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_close_list -> {
                    // Handle "Close List" action
                    showCloseMovementConfirmationPopup(
                        activity = requireActivity(),
                        titleText = getString(R.string.close_movement_list),
                        descriptionText = getString(R.string.are_you_sure_you_want_to_close_an_finalize_the_movement_list),
                        button1Text = getString(R.string.yes_close)
                    ) {
                        viewModel.onEvent(MovementsEvent.CloseMovement(movementsSharedViewModel.currentMovement?.id.toString()))
                    }
                    true
                }

                else -> false
            }
        }

        // Show the popup menu
        popup.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        movementsSharedViewModel.scannedArticle = null

    }


    override fun onViewClicked(data: MovementItem) {
        clickedMovementItem = data
        movementsSharedViewModel.movementActionType = MovementActionType.DROP_OFF
        movementsSharedViewModel.currentMovementItemToDropOff = data
        viewModel.onEvent(
            MovementsEvent.GetWarehouseStockyardInventoryEntries(
                articleId = data.articleId,
                stockyardId = data.sourceWarehouseStockYardId.toString(),
                warehouseCode = data.warehouseCode,
                isFromUserEntry = false
            )
        )

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