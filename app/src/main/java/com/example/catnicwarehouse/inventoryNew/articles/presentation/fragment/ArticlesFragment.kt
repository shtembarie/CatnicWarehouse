package com.example.catnicwarehouse.inventoryNew.articles.presentation.fragment

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult

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
import com.example.catnicwarehouse.databinding.FragmentArticles2Binding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter.InventoryItemsAdapter
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses.InventoryItemsViewState
import com.example.catnicwarehouse.inventoryNew.articles.presentation.viewModel.ArticlesViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.movement.articles.presentation.fragment.MovementArticlesFragmentDirections
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent

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
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.catnicwarehouse.utils.isEMDKAvailable
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
class ArticlesFragment : BaseFragment(), ScanEventListener {

    private var _binding: FragmentArticles2Binding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: InventoryItemsAdapter
    private val viewModel: ArticlesViewModel by viewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private var clickedInventoryItem: InventoryItem? = null

    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
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
        _binding = FragmentArticles2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleHeaderSection()
        setupAdapter()


        // Case: Normal
        if (inventorySharedViewModel.conflictingInventoryitems.isNullOrEmpty()) {
            binding.banner.visibility = View.GONE
            viewModel.onEvent(InventoryItemsEvent.InventoryItems(inventorySharedViewModel.selectedInventoryId.toString()))
            observeInventoryItemsEvents()
            handleButtonActions()
            //handle the result back from scan option bottom sheet
            handleScanOptionResultBack()
            //handle the result back from article scan success
            handleSuccessArticleScanResultBack()
        } else { // Case: Conflicting Inventory Items
            adapter.submitList(inventorySharedViewModel.conflictingInventoryitems)
            binding.scanStockyardsButton.visibility = View.GONE
            binding.buttonBack.visibility = View.GONE
            binding.banner.visibility = View.VISIBLE
        }

    }

    private fun handleHeaderSection() {
        binding.inventoryHeader.headerTitle.text =
            inventorySharedViewModel.scannedStockyard?.name ?: ""
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.rightToolbarButton.visibility = View.GONE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun observeInventoryItemsEvents() {
        viewModel.articlesFlow.onEach { state ->
            when (state) {
                InventoryItemsViewState.Empty -> progressBarManager.dismiss()
                is InventoryItemsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(it) }
                }

                is InventoryItemsViewState.InventoryItems -> {
                    progressBarManager.dismiss()
                    val inventoryItems = state.items
                    var filteredInventoryItems =
                        inventoryItems.filter { s -> s.warehouseStockYardId == inventorySharedViewModel.scannedStockyard?.id && s.inventoried }
                    filteredInventoryItems =
                        filteredInventoryItems.sortedWith(compareByDescending<InventoryItem> { it.inventoried }.thenBy { false })
                    inventorySharedViewModel.filteredInventoryItems = filteredInventoryItems
                    adapter.submitList(filteredInventoryItems)
                }

                InventoryItemsViewState.Loading -> progressBarManager.show()

                is InventoryItemsViewState.ArticleResult -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    inventorySharedViewModel.scannedArticle = state.articles[0]
                    handleSuccessArticleScanResultBackAction(
                        scanType,
                        isFromUserClickAction = false
                    )

                }

                is InventoryItemsViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()
                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (state.isFromUserEntry) {

                            val entries = state.warehouseStockyardInventoryEntriesResponse

                            val inventoriedArticleIds =
                                inventorySharedViewModel.filteredInventoryItems?.map { it.articleId }
                                    ?: emptySet()

                            // 2) Filter the entries whose articleId is in that set
                            val filteredEntries =
                                entries.filter { it.articleId in inventoriedArticleIds }
                                    ?: emptyList()

                            // In case only 1 item is  move to MatchFound screen
                            // else inform user about multiple articles and move to article selection screen
                            if (entries.count() == 1)
                                inventorySharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                        button1Text = getString(R.string.yes_select_item),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            inventorySharedViewModel.selectedArticle = entries[0]
                                            navigateToMatchFound()
                                        },
                                        button2Callback = {
                                            scannerHelper?.stopScanning()
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ARTICLE)
                                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                        })
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            else
                                inventorySharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                                        button1Text = getString(R.string.yes_continue),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            inventorySharedViewModel.articlesListToSelectFrom =
                                                entries
                                            navigateToArticleSelection()

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
                        if (state.isFromUserEntry) {
                            showErrorScanBottomSheet()
                        } else {
                            showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
                        }
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupAdapter() {
        binding.inventoryList.layoutManager = LinearLayoutManager(requireContext())
        adapter = InventoryItemsAdapter { inventoryItem ->
            // Case: Normal
            if (inventorySharedViewModel.conflictingInventoryitems.isNullOrEmpty()) {
                inventorySharedViewModel.scannedArticle = null
                inventorySharedViewModel.selectedInventoryItem = null
                clickedInventoryItem = inventoryItem
                handleSuccessArticleScanResultBackAction(scanType, isFromUserClickAction = true)
            } else {  // Case: Conflicting Inventory Item
                parentFragmentManager.setFragmentResult(
                    "conflictItemSelected",
                    bundleOf("item_id" to inventoryItem.id)
                )
                findNavController().popBackStack()
            }
        }
        binding.inventoryList.adapter = adapter
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
            moduleType = ModuleType.INVENTORY
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
            scanEventListener = this@ArticlesFragment
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
                viewModel.onEvent(InventoryItemsEvent.SearchArticle(data))
            }
        }
    }

    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!

            handleSuccessArticleScanResultBackAction(
                scanType = scanType,
                isFromUserClickAction = false
            )
        }
    }

    private fun handleSuccessArticleScanResultBackAction(
        scanType: ScanType,
        isFromUserClickAction: Boolean
    ) {
        if (isFromUserClickAction) {
            inventorySharedViewModel.selectedInventoryItem = clickedInventoryItem
            inventorySharedViewModel.scannedArticle = null
            navigateToMatchFound()
            return
        }
        inventorySharedViewModel.selectedInventoryItem = null
        inventorySharedViewModel.scannedArticle?.let {
            viewModel.onEvent(
                InventoryItemsEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = inventorySharedViewModel.scannedArticle?.articleId,
                    stockyardId = inventorySharedViewModel.scannedStockyard?.id.toString(),
                    warehouseCode = IncomingConstants.WarehouseParam,
                    isFromUserEntry = true
                )
            )
        }


//
//        val articleId =
//            inventorySharedViewModel.scannedArticle?.articleId ?: clickedInventoryItem?.articleId
//        val stockyardId = inventorySharedViewModel.scannedStockyard?.id
//            ?: clickedInventoryItem?.warehouseStockYardId
//        val warehouseCode = inventorySharedViewModel.scannedStockyard?.warehouseCode
//            ?: clickedInventoryItem?.warehouseCode ?: IncomingConstants.WarehouseParam
//
//
//        val matchedItems = inventorySharedViewModel.articleItemList?.filter { s ->
//            s.articleId == articleId &&
//                    s.warehouseStockYardId == stockyardId &&
//                    s.warehouseCode == warehouseCode
//        }
//
//        if (matchedItems.isNullOrEmpty()) {
//            if (isFromUserClickAction) {
//                showErrorScanBottomSheet()
//            } else {
//                showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
//            }
//            return
//        }
//
//
//        // In case only 1 item is matched show confirmation and move to MatchFound screen
//        // else inform user about multiple articles with same id and move to article selection screen
//        if (!isFromUserClickAction) {
//            if (matchedItems.count() == 1) {
//                val bottomSheet = SuccessScanBottomSheet.newInstance(
//                    titleText = matchedItems[0].articleMatchcode,
//                    descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
//                    button1Text = getString(R.string.yes_select_item),
//                    button2Text = getString(R.string.scan_again),
//                    button1Callback = {
//                        inventorySharedViewModel.selectedInventoryItem = matchedItems[0]
//                        val action =
//                            ArticlesFragmentDirections.actionArticlesFragment2ToMatchFragment()
//                        findNavController().navigate(action)
//
//                    },
//                    button2Callback = {
//                        scannerHelper?.stopScanning()
//                        val bottomSheet =
//                            ScanOptionsBottomSheet.newInstance(ARTICLE)
//                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//                    })
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//            } else {
//                val bottomSheet = SuccessScanBottomSheet.newInstance(
//                    titleText = matchedItems[0].articleMatchcode,
//                    descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
//                    button1Text = getString(R.string.yes_continue),
//                    button2Text = getString(R.string.scan_again),
//                    button1Callback = {
//                        inventorySharedViewModel.inventoryItemsListToSelectFrom = matchedItems
//                        val action =
//                            ArticlesFragmentDirections.actionArticlesFragment2ToArticleSelectionFragment5()
//                        findNavController().navigate(action)
//
//                    },
//                    button2Callback = {
//                        scannerHelper?.stopScanning()
//                        val bottomSheet =
//                            ScanOptionsBottomSheet.newInstance(ARTICLE)
//                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//                    })
//
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//            }
//        } else {
//            inventorySharedViewModel.selectedInventoryItem =
//                matchedItems.firstOrNull { s -> s.id == clickedInventoryItem?.id }
//            val action =
//                ArticlesFragmentDirections.actionArticlesFragment2ToMatchFragment()
//            findNavController().navigate(action)
//        }

    }

    private fun navigateToMatchFound() {
        val action =
            ArticlesFragmentDirections.actionArticlesFragment2ToMatchFragment()
        findNavController().navigate(action)

    }

    private fun navigateToArticleSelection() {
        val action =
            ArticlesFragmentDirections.actionArticlesFragment2ToArticleSelectionFragment5()
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        inventorySharedViewModel.scannedArticle = null
        clickedInventoryItem = null
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