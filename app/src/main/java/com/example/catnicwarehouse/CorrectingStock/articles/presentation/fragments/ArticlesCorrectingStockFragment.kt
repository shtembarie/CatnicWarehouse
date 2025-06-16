package com.example.catnicwarehouse.CorrectingStock.articles.presentation.fragments

import android.annotation.SuppressLint
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
import com.example.catnicwarehouse.CorrectingStock.articles.presentation.viewModel.WarehouseStockYardsArticleViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticlesCorrectingStockBinding
import com.example.catnicwarehouse.movement.articles.presentation.adapter.StockyardArticlesAdapter
import com.example.catnicwarehouse.movement.articles.presentation.adapter.WarehouseStockyardArticleAdapterInteraction
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.articles.presentation.viewModel.ArticlesViewModel
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.symbol.emdk.barcode.StatusData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_movement_summary.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ArticlesCorrectingStockFragment : BaseFragment(), WarehouseStockyardArticleAdapterInteraction,
    ScanEventListener {
    private var _binding: FragmentArticlesCorrectingStockBinding? = null
    private val binding get() = _binding!!

    private val warehouseStockYardsArticleViewModel: WarehouseStockYardsArticleViewModel by activityViewModels()
    private val corrStockSharedViewModel: CorrStockSharedViewModel by activityViewModels()

    private var isItemCorrected: Boolean = false

    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val viewModel: ArticlesViewModel by viewModels()
    private var clickedArticle: WarehouseStockyardInventoryResponseModel? = null
    private var scannerHelper: ScannerHelper? = null
    private var scanType: ScanType = ScanType.ARTICLE
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private lateinit var stockyardArticlesAdapter: StockyardArticlesAdapter

    private var scanPopupFragment: ScanActiveFragment? = null
    private var isScanUIButtonPressed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticlesCorrectingStockBinding.inflate(inflater, container, false)
        isItemCorrected = arguments?.getBoolean("isItemCorrected", false) ?: false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isItemCorrected()
        handleHeaderSection()

        setUpAdapter()
        val stockYardId = arguments?.getInt("stockyardId") ?: -1
        corrStockSharedViewModel.savestockyardId(stockYardId)
        viewModel.onEvent(ArticlesEvent.GetStockyardInventory(stockYardId.toString()))
        observeEvents()
        handleButtonActions()
        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from article scan success
        handleSuccessArticleScanResultBack()
    }
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun isItemCorrected() {
        // Check if the item is corrected and should show the design element
        if (corrStockSharedViewModel.isItemCorrected) {
            // Showing the new design element
            binding.newDesignElement.visibility = View.VISIBLE

            // Update the text for the corrected item
            val articleForNewDesign = corrStockSharedViewModel.articleForNewDesign
            val articleText = getString(R.string.article)
            val itemText = getString(R.string.item_text)
            val correctedText = getString(R.string.has_been_corrected)
            binding.itemTextArticle.text = "$articleText ($itemText $articleForNewDesign) $correctedText"

            // Mark item correction as handled to prevent re-showing until a new update
            corrStockSharedViewModel.isItemCorrected = false
        }

        // Set an OnTouchListener to hide the view when the user interacts with it
        binding.newDesignElement.setOnTouchListener { _, _ ->
            // Hide the element when touched
            binding.newDesignElement.visibility = View.GONE

            true // Return true to indicate the touch event was handled
        }
    }
    private fun handleHeaderSection(){
        binding.inventoryHeader.headerTitle.text = getString(R.string.articles)
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.rightToolbarButton.visibility = View.GONE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpAdapter() {
        stockyardArticlesAdapter =
            StockyardArticlesAdapter(interaction = this, requireContext(), movementSharedViewModel)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.inventoryList.layoutManager = layoutManager
        binding.inventoryList.adapter = stockyardArticlesAdapter
    }

    private fun observeEvents() {
        viewModel.articlesFlow.onEach { state ->
            when (state) {
                ArticlesViewState.Empty -> progressBarManager.dismiss()
                is ArticlesViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is ArticlesViewState.GetStockyardInventoryResult -> {
                    progressBarManager.dismiss()
                    stockyardArticlesAdapter.submitList(state.articles)
                    movementSharedViewModel.articlesList = state.articles

                    if (state.articles.isNullOrEmpty()){
                        binding.scanStockyardsButton.visibility = View.GONE
                        binding.buttonContinue.visibility = View.GONE
                        binding.constraintLayout.visibility = View.VISIBLE
                    }
                }

                ArticlesViewState.Loading -> progressBarManager.show()
                ArticlesViewState.Reset -> progressBarManager.dismiss()
                is ArticlesViewState.ArticleResult -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    movementSharedViewModel.scannedArticle = state.articles[0]
                    handleSuccessArticleScanResultBackAction(scanType)
                }

                is ArticlesViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()


                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (state.isFromUserEntry) {
                            // In case only 1 item is matched show confirmation and move to MatchFound screen
                            // else inform user about multiple articles with same id and move to article selection screen
                            if (state.warehouseStockyardInventoryEntriesResponse.count() == 1)
                                movementSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                        button1Text = getString(R.string.yes_select_item),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            val articleId = state.warehouseStockyardInventoryEntriesResponse[0].articleId.toString()
                                            corrStockSharedViewModel.saveArticleId(articleId)
                                            val matchCode = state.warehouseStockyardInventoryEntriesResponse[0].articleMatchCode.toString()
                                            corrStockSharedViewModel.saveMatchCode(matchCode)
                                            val amount = state.warehouseStockyardInventoryEntriesResponse[0].amount
                                            corrStockSharedViewModel.saveAmount(amount!!.toInt())
                                            val unitCode = state.warehouseStockyardInventoryEntriesResponse[0].unitCode
                                            corrStockSharedViewModel.saveUnitCode(unitCode.toString())
                                            val entryId = state.warehouseStockyardInventoryEntriesResponse[0].id
                                            corrStockSharedViewModel.saveEntryId(entryId!!.toInt())
                                            movementSharedViewModel.selectedArticle =
                                                state.warehouseStockyardInventoryEntriesResponse[0]
                                            val action =
                                                ArticlesCorrectingStockFragmentDirections.actionArticlesCorrectingStockFragmentToMatchFoundCorrectiveStockFragment()
                                            findNavController().navigate(action)

                                        },
                                        button2Callback = {
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                        })
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            else
                                movementSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                                        button1Text = getString(R.string.yes_continue),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            val articleId = state.warehouseStockyardInventoryEntriesResponse[0].articleId.toString()
                                            corrStockSharedViewModel.saveArticleId(articleId)
                                            val matchCode = state.warehouseStockyardInventoryEntriesResponse[0].articleMatchCode.toString()
                                            corrStockSharedViewModel.saveMatchCode(matchCode)
                                            val amount = state.warehouseStockyardInventoryEntriesResponse[0].amount
                                            corrStockSharedViewModel.saveAmount(amount!!.toInt())
                                            val unitCode = state.warehouseStockyardInventoryEntriesResponse[0].unitCode
                                            corrStockSharedViewModel.saveUnitCode(unitCode.toString())
                                            val entryId = state.warehouseStockyardInventoryEntriesResponse[0].id
                                            corrStockSharedViewModel.saveEntryId(entryId!!.toInt())
                                            movementSharedViewModel.articlesListToSelectFrom =
                                                state.warehouseStockyardInventoryEntriesResponse
                                            val action =
                                                ArticlesCorrectingStockFragmentDirections.actionArticlesCorrectingStockFragmentToArticleSelectionFragment4()
                                            findNavController().navigate(action)

                                        },
                                        button2Callback = {
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                        })

                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }


                        } else {
                            movementSharedViewModel.selectedArticle =
                                state.warehouseStockyardInventoryEntriesResponse.filter { s -> s.id == clickedArticle?.id && s.articleId == clickedArticle?.articleId }
                                    .get(0)
                            val articleId = state.warehouseStockyardInventoryEntriesResponse[0].articleId.toString()
                            corrStockSharedViewModel.saveArticleId(articleId)
                            val matchCode = state.warehouseStockyardInventoryEntriesResponse[0].articleMatchCode.toString()
                            corrStockSharedViewModel.saveMatchCode(matchCode)
                            val amount = state.warehouseStockyardInventoryEntriesResponse[0].amount
                            corrStockSharedViewModel.saveAmount(amount!!.toInt())
                            val unitCode = state.warehouseStockyardInventoryEntriesResponse[0].unitCode
                            corrStockSharedViewModel.saveUnitCode(unitCode.toString())
                            val entryId = state.warehouseStockyardInventoryEntriesResponse[0].id
                            corrStockSharedViewModel.saveEntryId(entryId!!.toInt())
                            movementSharedViewModel.selectedArticle?.let {
                                val action =
                                    ArticlesCorrectingStockFragmentDirections.actionArticlesCorrectingStockFragmentToMatchFoundCorrectiveStockFragment()
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
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onViewClicked(data: WarehouseStockyardInventoryResponseModel) {
        clickedArticle = data
        corrStockSharedViewModel.saveArticleId(data.articleId.toString())
        corrStockSharedViewModel.saveMatchCode(data.articleMatchCode.toString())
        corrStockSharedViewModel.saveArticleDescription(data.articleDescription.toString())
        corrStockSharedViewModel.saveAmount(data.amount!!.toInt())
        corrStockSharedViewModel.saveUnitCode(data.unit.toString())
        corrStockSharedViewModel.saveEntryId(data.id!!.toInt())
        viewModel.onEvent(
            ArticlesEvent.GetWarehouseStockyardInventoryEntries(
                articleId = data.articleId,
                stockyardId = arguments?.getInt("stockyardId").toString(),
                warehouseCode = movementSharedViewModel.scannedStockyard?.warehouseCode,
                isFromUserEntry = false
            )
        )


    }


    private fun handleButtonActions() {
        with(binding) {
            scanStockyardsButton.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            buttonContinue.setOnClickListener {
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
            when (bundle.getParcelable<ScanOptionEnum>("scanOption")!!) {
                ScanOptionEnum.BARCODE -> {
                    openScanner(ScannerType.DEFAULT_SCANNER)
                }

                ScanOptionEnum.CAMERA -> {
                    openScanner(ScannerType.CAMERA)
                }

                ScanOptionEnum.MANUAL -> {
                    openManualInputBottomSheet(scanType)
                }

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
            scanEventListener = this@ArticlesCorrectingStockFragment
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

    private fun updateData(data: String) {

        when (scanType) {
            ScanType.ONLY_STOCKYARD, ScanType.STOCKYARD -> {

            }

            ScanType.ARTICLE -> {

                viewModel.onEvent(ArticlesEvent.SearchArticle(data))
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

        movementSharedViewModel.scannedArticle?.let {
            viewModel.onEvent(
                ArticlesEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = movementSharedViewModel.scannedArticle?.articleId,
                    stockyardId = arguments?.getInt("stockyardId").toString(),
                    warehouseCode = movementSharedViewModel.scannedStockyard?.warehouseCode,
                    isFromUserEntry = true
                )
            )

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        movementSharedViewModel.scannedArticle = null
        clickedArticle = null
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


