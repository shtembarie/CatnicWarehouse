package com.example.catnicwarehouse.movement.articles.presentation.fragment


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
import com.example.catnicwarehouse.databinding.FragmentMovementArticlesBinding
import com.example.catnicwarehouse.movement.articles.presentation.adapter.StockyardArticlesAdapter
import com.example.catnicwarehouse.movement.articles.presentation.adapter.WarehouseStockyardArticleAdapterInteraction
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.articles.presentation.viewModel.ArticlesViewModel
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
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
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
class MovementArticlesFragment : BaseFragment(), WarehouseStockyardArticleAdapterInteraction,
    ScanEventListener {


    private var _binding: FragmentMovementArticlesBinding? = null
    private val binding get() = _binding!!
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val viewModel: ArticlesViewModel by viewModels()

    private lateinit var stockyardArticlesAdapter: StockyardArticlesAdapter
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var clickedArticle: WarehouseStockyardInventoryResponseModel? = null
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
        // Inflate the layout for this fragment
        _binding = FragmentMovementArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        viewModel.onEvent(ArticlesEvent.GetStockyardInventory(movementSharedViewModel.scannedStockyard?.id.toString()))
        observeEvents()
        handleButtonActions()
        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
        //handle the result back from article scan success
        handleSuccessArticleScanResultBack()
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            movementSharedViewModel.scannedStockyard?.name ?: getString(R.string.stockyard)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpAdapter() {
        stockyardArticlesAdapter =
            StockyardArticlesAdapter(interaction = this, requireContext(), movementSharedViewModel)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.adapter = stockyardArticlesAdapter
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

                            // 1) Collect all articleIds from inventory into a set
                            val inventoryArticleIds =
                                movementSharedViewModel.articlesList?.mapNotNull { it.articleId }
                                    ?: emptySet()

// 2) Filter the entries whose articleId is in that set
                            val filteredEntries =
                                state.warehouseStockyardInventoryEntriesResponse.filter { it.articleId in inventoryArticleIds }
                                    ?: emptyList()

                            // In case only 1 item is matched show confirmation and move to MatchFound screen
                            // else inform user about multiple articles with same id and move to article selection screen
                            if (filteredEntries.count() == 1)
                                movementSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_articles),
                                        button1Text = getString(R.string.yes_select_item),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            movementSharedViewModel.selectedArticle =
                                                filteredEntries[0]
                                            val action =
                                                MovementArticlesFragmentDirections.actionMovementArticlesFragmentToMatchFoundFragment3()
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
                            else
                                movementSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                                        button1Text = getString(R.string.yes_continue),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            movementSharedViewModel.articlesListToSelectFrom =
                                                filteredEntries
                                            val action =
                                                MovementArticlesFragmentDirections.actionMovementArticlesFragmentToArticleSelectionFragment()
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


                        } else {
                            movementSharedViewModel.selectedArticle =
                                state.warehouseStockyardInventoryEntriesResponse.filter { s -> s.id == clickedArticle?.id && s.articleId == clickedArticle?.articleId }
                                    .get(0)
                            movementSharedViewModel.selectedArticle?.let {
                                val action =
                                    MovementArticlesFragmentDirections.actionMovementArticlesFragmentToMatchFoundFragment3()
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
        viewModel.onEvent(
            ArticlesEvent.GetWarehouseStockyardInventoryEntries(
                articleId = data.articleId,
                stockyardId = movementSharedViewModel.scannedStockyard?.id.toString(),
                warehouseCode = movementSharedViewModel.scannedStockyard?.warehouseCode,
                isFromUserEntry = false
            )
        )


    }

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@MovementArticlesFragment
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

    private fun handleButtonActions() {
        with(binding) {
            scanArticlesButton.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            scanArticlesTextView.setOnClickListener {
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
                    stockyardId = movementSharedViewModel.scannedStockyard?.id.toString(),
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