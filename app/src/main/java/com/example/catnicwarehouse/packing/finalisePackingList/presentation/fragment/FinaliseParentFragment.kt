package com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentFinaliseParentBinding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.PackingTabAdapter
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.CancelPackingListBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.DropOffBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.FinaliseIncompletePackingListBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.viewModel.FinalisePackingViewModel
import com.example.catnicwarehouse.packing.packingItem.presentation.adapter.PackingItemsAdapterInteraction
import com.example.catnicwarehouse.packing.packingItem.presentation.bottomSheetFragment.PackingListCommentBottomSheet
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
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
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FinaliseParentFragment : BaseFragment(),
    FinaliseIncompletePackingListBottomSheet.FinaliseIncompletePackingListListener,
    CancelPackingListBottomSheet.CancelPackingListListener, ScanEventListener {

    private var _binding: FragmentFinaliseParentBinding? = null
    private val binding get() = _binding!!

    private val args: FinaliseParentFragmentArgs by navArgs()

    private var comment: String? = ""

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private val viewModel: FinalisePackingViewModel by viewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private lateinit var finaliseBottomSheet: FinaliseIncompletePackingListBottomSheet
    lateinit var cancelPackingListBottomSheet: CancelPackingListBottomSheet
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinaliseParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        setupCustomTabs()
        openTabAtIndex(1)
        observeFinalisePackingEvents()
        handleCommentIconAction()
        handleFinalisePackingListButtonAction()
        handleScanPositionButtonAction()
        handleScanOptionResultBack()
        handleSuccessArticleScanResultBack()
        handleSuccessDefaultDropzoneScanResultBack()
        viewModel.onEvent(FinalisePackingEvent.GetPackingListComment(id = args.packingId))
    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = args.packingId
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.setImageDrawable(requireContext().getDrawable(R.drawable.message_icon))
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCustomTabs() {
        val adapter = PackingTabAdapter(this)


        // Create a Bundle for the argument
        val packingArgs = Bundle().apply {
            putString("packingId", args.packingId)
        }

        adapter.addFragment(DetailsFragment(), "Details", packingArgs)
        adapter.addFragment(FinalisePackingFragment(), "Packing List", packingArgs)


        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = getCustomTabView(position, adapter.getFragmentTitle(position))

            // Apply specific backgrounds for left and right tabs
            if (position == 0) {
                tab.view.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_background_left_selector
                )
            } else if (position == 1) {
                tab.view.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_background_right_selector
                )
            }
        }.attach()
    }

    /**
     * Returns a custom view for the tab with dynamic title.
     */
    private fun getCustomTabView(position: Int, title: String): View {
        val customTabView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_tab_item, null)

        val tabTitle = customTabView.findViewById<TextView>(R.id.tabTitle)
        tabTitle.text = title

        return customTabView
    }

    private fun handleCommentIconAction() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            openCommentBottomSheet()
        }
    }

    private fun openCommentBottomSheet() {
        binding.deliveryHeader.iconBadge.visibility = View.GONE
        lifecycleScope.launch {
            dataStoreManager.savePackingListComment(comment ?: "")
        }
        val bottomSheet = PackingListCommentBottomSheet.newInstance(
            descriptionText = comment ?: ""
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun observeFinalisePackingEvents() {
        viewModel.finalisePackingFlow.onEach { state ->
            when (state) {
                FinalisePackingViewState.Empty -> progressBarManager.dismiss()
                is FinalisePackingViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                FinalisePackingViewState.Loading -> progressBarManager.show()
                FinalisePackingViewState.Reset -> progressBarManager.dismiss()
                is FinalisePackingViewState.ArticleResult -> {
                    progressBarManager.dismiss()
                }

                is FinalisePackingViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()

                    if (!state.warehouseStockyardInventoryEntriesResponse.isNullOrEmpty()) {
                        if (state.isFromUserEntry) {
                            // In case only 1 item is matched show confirmation and move to MatchFound screen
                            // else if matching articles >1, inform user about multiple articles with same id and move to article selection screen
                            //else show the error to the user
                            val matchingArticles =
                                getMatchingArticles(state.warehouseStockyardInventoryEntriesResponse)
                            if (matchingArticles.count() == 1) {
                                packingSharedViewModel.stockyardsListToSelectFrom = matchingArticles
                                packingSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.do_you_want_to_continue_picking_this_item),
                                        button1Text = getString(R.string.yes_select_item),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            packingSharedViewModel.selectedArticle =
                                                matchingArticles[0]
                                            packingSharedViewModel.selectedPackingItemToPack =
                                                packingSharedViewModel.packingItems?.filter { s -> s.articleId == matchingArticles[0].articleId }
                                                    ?.get(0)

                                            navigateToMatchFoundFragment()

                                        },
                                        button2Callback = {
                                            scannerHelper?.stopScanning()
                                            val bottomSheet =
                                                ScanOptionsBottomSheet.newInstance(ARTICLE)
                                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                        })
                                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                                }
                            } else if (matchingArticles.count() > 1) {
                                packingSharedViewModel.stockyardsListToSelectFrom = matchingArticles
                                packingSharedViewModel.scannedArticle?.matchCode?.let {
                                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                                        titleText = it,
                                        descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                                        button1Text = getString(R.string.yes_continue),
                                        button2Text = getString(R.string.scan_again),
                                        button1Callback = {
                                            packingSharedViewModel.articlesListToSelectFrom =
                                                matchingArticles
                                            val action =
                                                FinaliseParentFragmentDirections.actionFinalisePackingFragmentToArticleSelectionFragment2()
                                            action.packingId = args.packingId ?: ""
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
                                packingSharedViewModel.stockyardsListToSelectFrom = null
                                showErrorScanBottomSheet()
                            }

                        } else {
                            val matchingArticles =
                                getMatchingArticles(state.warehouseStockyardInventoryEntriesResponse)
                            packingSharedViewModel.selectedArticle = matchingArticles[0]
                            packingSharedViewModel.stockyardsListToSelectFrom = matchingArticles
                            navigateToMatchFoundFragment()
                        }
                    } else {
                        if (state.isFromUserEntry) {
                            showErrorScanBottomSheet()
                        } else {
                            showErrorBanner(getString(R.string.this_article_cannot_be_picked_up))
                        }
                    }
                }

                is FinalisePackingViewState.PausePackingResult -> {
                    progressBarManager.dismiss()
                    if (state.isPackingPaused == true) {
                        findNavController().popBackStack(R.id.packingListFragment, false)
                    }
                }

                is FinalisePackingViewState.GetPackingItemsResult -> {
                    progressBarManager.dismiss()
                }

                is FinalisePackingViewState.GetFinalizePackingListResult -> {
                    progressBarManager.dismiss()
                    if (state.isPackingListFinalized == true)
                        findNavController().popBackStack(R.id.packingListFragment, false)
                }

                is FinalisePackingViewState.GetCancelPackingListResult -> {
                    progressBarManager.dismiss()
                    if (state.isPackingListCancelled == true) {
                        findNavController().popBackStack(R.id.packingListFragment, false)
                    }
                }

                is FinalisePackingViewState.GetPackingListComment -> {
                    progressBarManager.dismiss()
                    comment = state.comment
                    lifecycleScope.launch {
                        val savedComment = dataStoreManager.getPackingListComment()
                        if (savedComment != comment && comment?.isNotBlank() == true) {
                            binding.deliveryHeader.iconBadge.visibility = View.VISIBLE
                        } else {
                            binding.deliveryHeader.iconBadge.visibility = View.GONE
                        }
                    }

                }

                else -> {
                    progressBarManager.dismiss()
                }

            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToMatchFoundFragment() {
        val action =
            FinaliseParentFragmentDirections.actionFinalisePackingFragmentToMatchFoundFragment2()
        findNavController().navigate(
            action
        )
        viewModel.onEvent(FinalisePackingEvent.Empty)
    }

    private fun getMatchingArticles(warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>): ArrayList<WarehouseStockyardInventoryEntriesResponseModel> {
        // Create a set of articleIds from packingItems for faster lookup
        if (packingSharedViewModel.packingItems != null) {
            val packingItemArticleIds =
                packingSharedViewModel.packingItems!!.mapNotNull { it.articleId }.toSet()

            // Filter the warehouseStockyardInventoryEntriesResponse based on matching articleId
            return ArrayList(
                warehouseStockyardInventoryEntriesResponse.filter { it.articleId in packingItemArticleIds }
            )
        }
        return warehouseStockyardInventoryEntriesResponse
    }

    private fun handleFinalisePackingListButtonAction() {
        binding.finalisePackingListButton.setOnClickListener {
            if (countOpenItems() > 0) {
                finaliseBottomSheet = FinaliseIncompletePackingListBottomSheet.newInstance(this)
                finaliseBottomSheet.show(
                    parentFragmentManager,
                    "FinaliseIncompletePackingListBottomSheet"
                )
            } else {
                viewModel.onEvent(FinalisePackingEvent.FinalizePackingList(args.packingId))
            }
        }
    }

    private fun handleScanPositionButtonAction() {
        with(binding) {
            scanPositionButton.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
            scanPositionTextView.setOnClickListener {
                scannerHelper?.stopScanning()
                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

        }
    }

    private fun showBanner() {

        if (countOpenItems() > 0)
            showInformationBanner(
                getString(
                    R.string.still_have_positions_pending_in_the_list,
                    countOpenItems().toString()
                )
            )
        else
            showPositiveBanner(getString(R.string.ready_for_packing))
    }


    private fun countOpenItems(): Int {
        return packingSharedViewModel.packingItems
            ?.count { item ->
                val shippingContainers = item.shippingContainers // Create a local reference
                (item.amount != item.packedAmount) || // Item has amount left to be packed
                        shippingContainers == null || // No shipping containers assigned
                        shippingContainers.none { it.packingListId == args.packingId } // No valid shipping container
            }
            ?: 0
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
            moduleType = ModuleType.PACKING_1
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

    @SuppressLint("SuspiciousIndentation")
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

    private fun openScanActivePopup() {
        isScanUIButtonPressed = false
        if (scanPopupFragment?.isVisible == true) {
            return // Avoid creating duplicate popups
        }
        scanPopupFragment = ScanActiveFragment().apply {
            scanEventListener = this@FinaliseParentFragment
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


    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            handleSuccessArticleScanResultBackAction(scanType)
        }
    }

    private fun handleSuccessArticleScanResultBackAction(scanType: ScanType) {
        packingSharedViewModel.scannedArticle?.let {
            viewModel.onEvent(
                FinalisePackingEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = packingSharedViewModel.scannedArticle?.articleId,
                    stockyardId = "",
                    warehouseCode = IncomingConstants.WarehouseParam,
                    isFromUserEntry = true
                )
            )
        }
    }


    private fun handleSuccessDefaultDropzoneScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessDropzoneScan", viewLifecycleOwner
        ) { _, bundle ->
            val dropzoneId = bundle.getInt("dropzoneId", 0)
            handleSuccessDefaultDropzoneScanResultBackAction(dropzoneId)
        }
    }


    private fun handleSuccessDefaultDropzoneScanResultBackAction(
        dropzoneId: Int
    ) {
        val bottomSheet = DropOffBottomSheet.newInstance(
            titleText = getString(R.string.drop_off_items),
            descriptionText = getString(R.string.are_you_sure_you_want_to_unpack_all_the_items_on_your_packing_list_here),
            button1Text = getString(R.string.drop_off_here),
            button2Text = getString(R.string.back),
            button1Callback = {
                viewModel.onEvent(
                    FinalisePackingEvent.CancelPackingList(
                        args.packingId,
                        CancelPackingRequestModel(dropZoneWarehouseStockYardId = dropzoneId)
                    )
                )
            },
            button2Callback = {

            })
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }



    override fun onCloseClicked() {
        finaliseBottomSheet.dismiss()
        viewModel.onEvent(FinalisePackingEvent.FinalizePackingList(args.packingId))
    }

    override fun onPauseClicked() {
        finaliseBottomSheet.dismiss()
        viewModel.onEvent(FinalisePackingEvent.PausePacking(args.packingId))
    }

    override fun onCancelClicked() {
        finaliseBottomSheet.dismiss()
        cancelPackingListBottomSheet = CancelPackingListBottomSheet.newInstance(this)
        cancelPackingListBottomSheet.show(
            parentFragmentManager,
            "CancelPackingListBottomSheet"
        )
    }

    override fun onBackClicked() {
        finaliseBottomSheet.dismiss()
    }

    override fun onCancelButtonFromCancelPackingListClicked() {
        cancelPackingListBottomSheet.dismiss()
        val action =
            FinaliseParentFragmentDirections.actionFinalisePackingFragmentToDropzoneFragment()
        findNavController().navigate(action)
    }

    override fun onBackButtonFromCancelPackingListClicked() {
        cancelPackingListBottomSheet.dismiss()
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
        viewModel.onEvent(FinalisePackingEvent.Empty)
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
        packingSharedViewModel.scannedArticle = null
    }

    /**
     * Opens a specific tab by its index.
     */
    private fun openTabAtIndex(index: Int) {
        // Set the current item in ViewPager2
        binding.viewPager.setCurrentItem(index, false) // `false` disables animation
        // Select the tab in TabLayout
        binding.tabLayout.getTabAt(index)?.select()
    }
}