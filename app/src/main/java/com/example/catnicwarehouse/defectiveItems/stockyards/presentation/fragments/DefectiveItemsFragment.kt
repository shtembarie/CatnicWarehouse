package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.fragments

import android.annotation.SuppressLint
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
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.bottomSheet.BottomSheetHelper
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDefectiveItemsBinding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.DefectiveArticlesAdapter
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter.DefectiveItemsAdapterInteraction
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses.GetDefectiveArticlesEvent
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses.GetDefectiveArticlesViewState
import com.example.catnicwarehouse.defectiveItems.stockyards.presentation.viewModel.DefectiveArticleViewModel
import com.example.catnicwarehouse.movement.articles.presentation.adapter.StockyardArticlesAdapter
import com.example.catnicwarehouse.movement.articles.presentation.adapter.WarehouseStockyardArticleAdapterInteraction
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.movement.articles.presentation.viewModel.ArticlesViewModel
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.symbol.emdk.barcode.StatusData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_inventory.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DefectiveItemsFragment : BaseFragment(), ScanEventListener,
    WarehouseStockyardArticleAdapterInteraction {
    private var _binding: FragmentDefectiveItemsBinding? = null
    private val binding get() = _binding!!
    private val defectiveArticleViewModel: DefectiveArticleViewModel by viewModels()
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private lateinit var defectiveArticlesAdapter: DefectiveArticlesAdapter
    private var scanType: ScanType = ScanType.ARTICLE
    private var scanTypeStockyard: ScanType = ScanType.STOCKYARD
    private var scanPopupFragment: ScanActiveFragment? = null
    private var isScanUIButtonPressed = false
    private var scannerHelper: ScannerHelper? = null
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private val viewModel: ArticlesViewModel by viewModels()
    private var clickedArticle: WarehouseStockyardInventoryResponseModel? = null
    private lateinit var stockyardArticlesAdapter: StockyardArticlesAdapter
    private var scannerType: ScannerType? = null

    private var isItemCorrected: Boolean = false

    private var searchForStockyard: List<GetDefectiveArticleUIModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defectiveArticleSharedViewModel.initViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDefectiveItemsBinding.inflate(inflater, container, false)
        isItemCorrected = arguments?.getBoolean("isItemCorrected", false) ?: false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        handleHeaderSection()
        closeListSection()
        observeNavigationResponse()
        handleButtonActions()
        handleSuccessArticleScanResultBack()
        handleScanOptionResultBack()
        isItemCorrected()
        handleSuccessStockyardScanResultBack()
        defectiveArticleViewModel.onEvent(GetDefectiveArticlesEvent.Loading(defectiveArticleSharedViewModel.warehouseCode))

    }
    private fun handleHeaderSection(){
        binding.inventoryHeader.headerTitle.text = getString(R.string.defective_article)
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.rightToolbarButton.visibility = View.GONE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().finish()
        }
    }
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun isItemCorrected() {
        // Check if the item is corrected and should show the design element
        if (defectiveArticleSharedViewModel.isItemCorrected) {
            // Showing the new design element
            binding.newDesignElement.visibility = View.VISIBLE

            // Update the text for the corrected item
            val articleForNewDesign = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.articleId ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.articleId
            val articleText = getString(R.string.article)
            val itemText = getString(R.string.item_text)
            val correctedText = getString(R.string.marked_as_defective)
            binding.itemTextArticle.text = "$articleText ($itemText $articleForNewDesign) $correctedText"

            // Mark item correction as handled to prevent re-showing until a new update
            defectiveArticleSharedViewModel.isItemCorrected = false
        }

        // Set an OnTouchListener to hide the view when the user interacts with it
        binding.newDesignElement.setOnTouchListener { _, _ ->
            // Hide the element when touched
            binding.newDesignElement.visibility = View.GONE

            true // Return true to indicate the touch event was handled
        }
    }
    private fun closeListSection(){
        binding.closeListButton.setOnClickListener {
            BottomSheetHelper.showBottomSheetDialog(
                requireContext(),
                onNewIncomingClick = {
                    requireActivity().finish()
                },
                onBackClick = {
                    // Handle back button action
                }
            )
        }
        binding.closeListTextView.setOnClickListener {
            BottomSheetHelper.showBottomSheetDialog(
                requireContext(),
                onNewIncomingClick = {
                    requireActivity().finish()
                },
                onBackClick = {
                    // Handle back button action
                }
            )
        }
    }
    private fun setAdapter(){
        // Initialize the adapter with interaction and context
        defectiveArticlesAdapter = DefectiveArticlesAdapter(
            interaction = object : DefectiveItemsAdapterInteraction {
                override fun onStockyardClicked(defectiveArticles: GetDefectiveArticleUIModel) {
                    // Handle item click event
                    // For example, navigate to another fragment or perform an action
                    defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel = defectiveArticles
                    val action = defectiveArticles.id?.let {
                        DefectiveItemsFragmentDirections
                            .actionDefectiveItemsFragmentToMatchFoundDefectiveItemsFragment(it)
                    }
                    if (action != null) {
                        findNavController().navigate(action)
                    }
                }
            },
            context = requireContext(),
        )
        // Set up RecyclerView
        binding.stockyardsList.layoutManager = LinearLayoutManager(requireContext())
        binding.stockyardsList.adapter = defectiveArticlesAdapter
    }
    private fun setupAdapter(stockyards: List<GetDefectiveArticleUIModel>) {
        // Filter the stockyards to include only those with status "OPEN"
        val filteredStockyards = stockyards.filter { it.status == "OPEN" || it.status == "open" }

        // Submit the filtered list to the adapter
        defectiveArticlesAdapter.submitList(filteredStockyards)

        // Show or hide the scan button based on the filtered list
//        binding.scanStockyardsButton.visibility = if (filteredStockyards.isEmpty()) View.GONE else View.VISIBLE
        binding.closeListButton.visibility = if (filteredStockyards.isEmpty()) View.GONE else View.VISIBLE
        binding.constraintLayout.visibility = if (filteredStockyards.isEmpty()) View.VISIBLE else View.GONE
    }
    private fun observeNavigationResponse(){
        defectiveArticleViewModel.getDefectiveArticle.onEach { state ->
            when(state){
                GetDefectiveArticlesViewState.Empty -> {progressBarManager.dismiss()}
                is GetDefectiveArticlesViewState.Error -> {progressBarManager.dismiss()
                state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }}
                GetDefectiveArticlesViewState.Reset -> {progressBarManager.dismiss()}
                is GetDefectiveArticlesViewState.DefectiveArticles -> {
                    progressBarManager.dismiss()
                    val stockyards = state.defectiveArticles
                    if (stockyards.isEmpty()){
                        // Show empty layout if no stockyards
                        binding.stockyardsList.visibility = View.GONE
                        binding.constraintLayout.visibility = View.VISIBLE
                        //binding.newDesignElement.visibility = View.GONE
                    } else {
                        // Show RecyclerView and hide empty layout
                        binding.stockyardsList.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.GONE
                        //binding.newDesignElement.visibility = View.VISIBLE
                       //val stockyards = state.WarehouseCode
                        searchForStockyard = stockyards // Update currentInventory
                        setupAdapter(stockyards)
                    }
                    defectiveArticleSharedViewModel.defectiveItemIdsAndArticleIds = state.defectiveArticles
                }
                GetDefectiveArticlesViewState.Loading -> {progressBarManager.show()}
                is GetDefectiveArticlesViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty() ){
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    if (state.warehouseStockyardInventoryEntriesResponse.count() == 1 ){
                        val bottomSheet = SuccessScanBottomSheet.newInstance(
                            titleText = state.titleText,
                            descriptionText = getString(R.string.would_like_to_continue_selecting_this_location),
                            button1Text = getString(R.string.yes_continue),
                            button2Text = getString(R.string.scan_again),
                            button1Callback = {
                                defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry = state.warehouseStockyardInventoryEntriesResponse[0]

                                val action = DefectiveItemsFragmentDirections.actionDefectiveItemsFragmentToMatchFoundDefectiveItemsFragment(state.warehouseStockyardInventoryEntriesResponse[0].stockYardId ?: 0 )
                                findNavController().navigate(action)
                            },
                            button2Callback = {
                                val bottomSheet =
                                    ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                            })
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    } else if (state.warehouseStockyardInventoryEntriesResponse.count() > 1){

                        val bottomSheet = SuccessScanBottomSheet.newInstance(
                            titleText = state.titleText,
                            descriptionText = getString(R.string.multiple_stockyards_found_would_you_like_to_select_one_from_them),
                            button1Text = getString(R.string.yes_continue),
                            button2Text = getString(R.string.scan_again),
                            button1Callback = {
                                defectiveArticleSharedViewModel.warehouseStockyardInventoryEntry = state.warehouseStockyardInventoryEntriesResponse
                                  val action = DefectiveItemsFragmentDirections.actionDefectiveItemsFragmentToStockyardSelectionFragment()
                                findNavController().navigate(action)

                            },
                            button2Callback = {
                                val bottomSheet =
                                    ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                            }
                        )
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)

                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    /** Implementing the Scanner Device */
    private fun handleButtonActions() {
        with(binding) {
            scanStockyardsButton.setOnClickListener {
                scannerHelper?.stopScanning()
                defectiveArticleSharedViewModel.initViewModel()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            scanStockyardsTextView.setOnClickListener {
                scannerHelper?.stopScanning()
                defectiveArticleSharedViewModel.initViewModel()
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
            moduleType = ModuleType.DEFECTIVE_ITEMS
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
            scannerHelper = ScannerHelper(
                context = requireContext(),
                scannerType = scannerType,
                updateStatus = { status, scannerState -> updateStatus(status, scannerState) },
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
            scanEventListener = this@DefectiveItemsFragment
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
    private fun updateData(data: String) {
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        scannerHelper = null
        when (scanType) {
            ScanType.ONLY_STOCKYARD, ScanType.STOCKYARD -> {
                val stockyardId = data.toIntOrNull()
                if (stockyardId != null) {
                    val filteredItems = searchForStockyard
                        ?.filter { it.id == stockyardId }  ?: emptyList() // Match based on stockyardId
                    if (filteredItems.isNotEmpty()) {
                        val stockyardName = filteredItems.firstOrNull()?.warehouseName ?: "Stockyard"

                        val bottomSheet = SuccessScanBottomSheet.newInstance(
                            titleText = stockyardName,
                            descriptionText = getString(R.string.would_like_to_continue_selecting_this_location),
                            button1Text = getString(R.string.yes_continue),
                            button2Text = getString(R.string.scan_again),
                            button1Callback = {
                                val action =
                                    DefectiveItemsFragmentDirections
                                        .actionDefectiveItemsFragmentToArticleSelectionFragment42()
                                findNavController().navigate(action)
                            },
                            button2Callback = {
                                val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                            }
                        )
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    } else {
                        val errorBottomSheet = ErrorScanBottomSheet.newInstance(
                            titleText = getString(R.string.unknown_bar_code),
                            descriptionText = getString(R.string.popup_description),
                            buttonText = getString(R.string.scan_again),
                            button1Callback = {
                                val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                                scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
                            }
                        )
                        errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
                    }

                } else {
                    val errorBottomSheet = ErrorScanBottomSheet.newInstance(
                        titleText = getString(R.string.unknown_bar_code),
                        descriptionText = getString(R.string.popup_description),
                        buttonText = getString(R.string.scan_again),
                        button1Callback = {
                            val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                            scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
                        }
                    )
                    errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
                }
            }
            ScanType.ARTICLE -> {
                viewModel.onEvent(ArticlesEvent.SearchArticle(data))
            }
        }
    }
    private fun updateStatus(status: String, scannerStates: StatusData.ScannerStates) {

        if (scanPopupFragment != null && scanPopupFragment!!.isAdded && scanPopupFragment!!.isResumed) {
            when (scannerStates) {
                StatusData.ScannerStates.WAITING, StatusData.ScannerStates.IDLE -> {
                    if (isScanUIButtonPressed) {
                        scanPopupFragment?.updateTitle(
                            getString(R.string.scanning)
                        )
                    } else {
                        scanPopupFragment?.updateTitle(
                            getString(R.string.scanner_is_ready)
                        )
                    }
                }

                StatusData.ScannerStates.SCANNING -> scanPopupFragment?.updateTitle(
                    getString(R.string.scanning)
                )

                StatusData.ScannerStates.DISABLED -> scanPopupFragment?.updateTitle(getString(R.string.scanner_is_disabled))
                StatusData.ScannerStates.ERROR -> scanPopupFragment?.updateTitle(getString(R.string.error_occured_while_scanning))
            }
        }

    }
    override fun onViewClicked(data: WarehouseStockyardInventoryResponseModel) {
        clickedArticle = data
        val action = DefectiveItemsFragmentDirections.actionDefectiveItemsFragmentToMatchFoundDefectiveItemsFragment(data.stockYardId ?: 0)
        findNavController().navigate(action)
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
        val bottomSheet = SuccessScanBottomSheet.newInstance(
            titleText = defectiveArticleSharedViewModel.scannedArticle?.articleId ?: "",
            descriptionText = getString(R.string.do_you_want_to_continue_selecting_this_item_as_defective),
            button1Text = getString(R.string.yes_continue),
            button2Text = getString(R.string.scan_again),
            button1Callback = {
                showScanOptionsBottomSheet(scanTypeStockyard)
            },
            button2Callback = {
                val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.STOCKYARD)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        clickedArticle = null
    }
    /** Implementing to Scann for Stockyard after checking for an Article*/
    private fun showScanOptionsBottomSheet(scanTypeStockyard: ScanType) {
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanTypeStockyard)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }
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
    @SuppressLint("SuspiciousIndentation")
    private fun handleSuccessStockyardScanResultBackAction(
        titleText: String?,
        stockyardId: Int?,
        scanType: ScanType?
    ) {
        if (scanType == ScanType.STOCKYARD) {
            titleText?.let { stockyardIdString ->

                if (stockyardId != null ) {
                    defectiveArticleViewModel.onEvent(GetDefectiveArticlesEvent.GetWarehouseStockyardInventoryEntries(articleId = defectiveArticleSharedViewModel.scannedArticle?.articleId,
                    stockyardId = stockyardId.toString(), warehouseCode = defectiveArticleSharedViewModel.warehouseCode, titleText = titleText))

                } else {
                    showErrorScanBottomSheet()
                }
            }
        } else if (scanType == ScanType.ONLY_STOCKYARD) {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryItemsFragment)
        }
    }


}