package com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.adapter.InventoryItemsAdapter
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses.MatchFoundInventoryItemEvent
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.viewModel.InventoryItemsViewModel
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.viewModel.MatchFoundInventoryViewModel
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentInventoryItemsBinding
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ManualInputBottomSheet
import com.example.catnicwarehouse.scan.presentation.bottomSheetFragment.ScanOptionsBottomSheet
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.fragment.ScanActiveFragment
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.bottomSheet.SuccessScanBottomSheet
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.symbol.emdk.barcode.StatusData.ScannerStates
import com.symbol.emdk.barcode.StatusData.ScannerStates.DISABLED
import com.symbol.emdk.barcode.StatusData.ScannerStates.ERROR
import com.symbol.emdk.barcode.StatusData.ScannerStates.IDLE
import com.symbol.emdk.barcode.StatusData.ScannerStates.SCANNING
import com.symbol.emdk.barcode.StatusData.ScannerStates.WAITING
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class InventoryItemsFragment : BaseFragment(), ScanEventListener {
    private var _binding: FragmentInventoryItemsBinding? = null
    private val binding get() = _binding!!
    private val inventoryItemsViewModel: InventoryItemsViewModel by activityViewModels()
    private val viewModelItems: MatchFoundInventoryViewModel by viewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private var scannerHelper: ScannerHelper? = null
    private var scanType: ScanType = ScanType.ARTICLE
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var filteredItems: List<InventoryItem> = emptyList()
    private lateinit var inventoryItemsAdapter: InventoryItemsAdapter
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false
    private val args: InventoryItemsFragmentArgs by navArgs()
    private var stockyardId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        observeNavigationResponse()
        stockyardId = args.stockyardId
        val savedId = inventorySharedViewModel.clickedStockyardId
        if (savedId != null) {
            inventoryItemsViewModel.getInventoryItems(savedId)
        }
        binding.inventoryList.layoutManager = LinearLayoutManager(requireContext())
        inventoryItemsAdapter = InventoryItemsAdapter(mutableListOf(), { itemId ->
        }, inventorySharedViewModel)
        setupInventoryBottomSheet()
        handleSuccessArticleScanResultBack()
        handleScanOptionResultBack()
        handleArgsUpdateFromMatchFoundResultBack()
        Log.d("scanType1", scanType.name)
    }

    private fun handleHeaderSection() {
        val passedStockyardName = arguments?.getString("stockyardName")
        val stockyardNameToShow = passedStockyardName
        val headerTextFinal = if (stockyardNameToShow != null) {
            "$stockyardNameToShow"
        } else {
            ""
        }

        binding.inventoryHeader.headerTitle.text = headerTextFinal
        binding.inventoryHeader.toolbarSection.visibility = View.VISIBLE
        binding.inventoryHeader.leftToolbarButton.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun observeNavigationResponse() {
        val inventoryObserver: (GetInventoryByIdViewState) -> Unit = { state ->
            when (state) {
                is GetInventoryByIdViewState.Error -> {
                    progressBarManager.dismiss()
                }

                GetInventoryByIdViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                GetInventoryByIdViewState.Loading -> {
                    binding.currentState.checkInventoriedItems.visibility = View.GONE
                    binding.inventoryList.visibility = View.GONE
                    binding.noInventory.noStockyards.visibility = View.GONE
                    binding.scanStockyardsButton.visibility = View.GONE
                    binding.buttonBack.visibility = View.GONE
                    progressBarManager.show()
                }

                GetInventoryByIdViewState.Reset -> {
                    progressBarManager.show()
                }

                is GetInventoryByIdViewState.InventoryItemUpdated -> {
                    progressBarManager.dismiss()
                    if (state.isItemUpdated == true) {
                        findNavController().popBackStack()
                    }
                }

                is GetInventoryByIdViewState.GetInventoriesItems -> {
                    displayFilteredItems(state.items)
                    progressBarManager.dismiss()
                }

                GetInventoryByIdViewState.InventoryArticleItemAdded -> {
                    inventoryItemsViewModel.onItemEvent(InventoryItemsEvent.LoadInventory)
                    progressBarManager.show()
                }

                is GetInventoryByIdViewState.GetCurrentInventory -> {
                    progressBarManager.dismiss()
                }

                is GetInventoryByIdViewState.ArticlesForInventoryFound -> {
                    progressBarManager.dismiss()
                }

                is GetInventoryByIdViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                }
            }
        }
        viewModelItems.matchFoundFlow.onEach(inventoryObserver)
            .launchIn(viewLifecycleOwner.lifecycleScope)
        inventoryItemsViewModel.getInventoryItems.onEach(inventoryObserver)
            .launchIn(viewLifecycleOwner.lifecycleScope)
        binding.buttonBack.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun displayFilteredItems(items: List<InventoryItem>) {

        filteredItems = items.filter { s -> s.warehouseStockYardId == stockyardId }
        //        ?: arguments?.getParcelableArray("inventoryItems")?.map { it as InventoryItem }
//            ?: emptyList()
        if (filteredItems.isNotEmpty()) {
            val adapter = InventoryItemsAdapter(
                filteredItems as MutableList<InventoryItem>,
                { id ->

                    val item = filteredItems.find { it.id == id }
                    val bundle = Bundle().apply {
                        putInt("inventoryArticle", item?.id ?: 0)
                        putString("articleId", item?.articleId)
                        putString("articleDescription", item?.articleDescription)
                        putInt("amount", item?.actualStock ?: 0)
                        putString("comment", item?.comment)
                        putString("matchCode", item?.articleMatchcode)
                        putString(
                            "unitCode",
                            item?.actualUnitCode ?: item?.targetUnitCode
                            ?: item?.baseUnitCode
                        )
                    }
                    item?.let { navigateToArticleDescriptionFragment(bundle, it) }

                },
                inventorySharedViewModel
            )
            binding.inventoryList.adapter = adapter
            binding.noInventory.noStockyards.visibility = View.GONE
            binding.inventoryList.visibility = View.VISIBLE
            binding.currentState.checkInventoriedItems.visibility = View.GONE
            binding.scanStockyardsButton.visibility = View.VISIBLE
            binding.buttonBack.visibility = View.VISIBLE
        } else {
            binding.noInventory.noStockyards.visibility = View.VISIBLE
            binding.inventoryList.visibility = View.GONE
            binding.currentState.checkInventoriedItems.visibility = View.GONE
            binding.scanStockyardsButton.visibility = View.VISIBLE
        }
        inventoryItemsViewModel.totalActualStock.observe(viewLifecycleOwner) { total ->
            binding.currentState.checkInventoriedItems.findViewById<TextView>(R.id.tv_inventoried2).text =
                total.toString()
        }
        inventoryItemsViewModel.totalTargetStock.observe(viewLifecycleOwner) { total ->
            binding.currentState.checkInventoriedItems.findViewById<TextView>(R.id.tv_inventoried).text =
                total.toString()
        }
    }

    private fun showBottomSheetDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.finalize_inventory, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        bottomSheetDialog.setContentView(dialogView)

        val passedStockYardName = arguments?.getString("stockyardName")
        val stockyardTitle = if (passedStockYardName != null) {
            getString(R.string.finalize_stockyard)
        } else {
            getString(R.string.finalize_stockyard)
        }
        val titleTextView: TextView = dialogView.findViewById(R.id.title_popup_finalize)
        titleTextView.text = stockyardTitle
        val locationSection: TextView = dialogView.findViewById(R.id.title_id)
        val locationArea: TextView = dialogView.findViewById(R.id.decsription_id)
        val arrow: ImageView = dialogView.findViewById(R.id.to_right_btn)
        val totalAmountSection: TextView = dialogView.findViewById(R.id.total_amount_tv)

        totalAmountSection.text = getString(R.string.total_articles)
        locationSection.text = getString(R.string.location)
        locationArea.text = passedStockYardName
        arrow.visibility = View.GONE

        val totalItemAmount: TextView = dialogView.findViewById(R.id.to_right_btn2)
        totalItemAmount.text = inventorySharedViewModel.totalUpdatedItemAmount.toString()


        dialogView.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            progressBarManager.show()
            updateAllArticles {
                updateAllArticles {
                    inventorySharedViewModel.reset()
                    bottomSheetDialog.dismiss()
                    progressBarManager.show()
                }

            }
            progressBarManager.show()
        }
        dialogView.findViewById<TextView>(R.id.backButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun updateAllArticles(onComplete: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val stockyardId = inventorySharedViewModel.clickedStockyardId
            val inventoryItemsList = ArrayList<Pair<Int, SetInventoryItems>>()
            for (item in filteredItems) {
                val itemId = item.id
                if (itemId == 0) {
                    Log.e("UpdateAllArticles", "Item with ID 0 found: $item")
                    continue
                }
                val actualUnitCode = inventorySharedViewModel.updatedActualUnitCode[itemId]
                    ?: inventorySharedViewModel.unitCodeActual
                    ?: inventorySharedViewModel.targetUnitCode
                val actualStock =
                    inventorySharedViewModel.updatedItemAmount[itemId].takeIf { it != -1 }
                        ?: item.actualStock
                val comment = inventorySharedViewModel.updatedItemComment[itemId] ?: item.comment

                val setInventoryItems = SetInventoryItems(
                    actualUnitCode = actualUnitCode.toString(),
                    comment = comment,
                    actualStock = actualStock
                )
                inventoryItemsList.add(Pair(itemId, setInventoryItems))
            }
            stockyardId?.let {
                MatchFoundInventoryItemEvent.UpdateInventoryItems(
                    it,
                    inventoryItemsList
                )
            }?.let {
                viewModelItems.onEvent(
                    it
                )
            }
            withContext(Dispatchers.Main) {
                onComplete()
                progressBarManager.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Resetting the state when leaving the fragment, so it shows again when the fragment is recreated.
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_new_design_element", false).apply()
        scannerHelper?.stopScanning()
        scannerHelper?.onClosed()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_error, null)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.backButton).setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            val clickedItemId = inventorySharedViewModel.itemId

            inventorySharedViewModel.clickedArticleComment?.let {
                if (clickedItemId != null) {
                    inventorySharedViewModel.saveUpdatedComment(clickedItemId, it)
                }
            }

            inventorySharedViewModel.clickedItemIdActualStock?.let {
                if (clickedItemId != null) {
                    inventorySharedViewModel.saveUpdatedItemAmount(clickedItemId, it)
                }
            }

            inventorySharedViewModel.reset()
            alertDialog.dismiss()
            findNavController().popBackStack()
        }
        alertDialog.show()
    }

    private fun setupInventoryBottomSheet() {
        binding.buttonContinue.setOnClickListener {
            showScanOptionsBottomSheet(scanType)
        }
        binding.scanStockyardsButton.setOnClickListener {
            showScanOptionsBottomSheet(scanType)
        }
    }

    private fun showScanOptionsBottomSheet(scanType: ScanType) {
        scannerHelper?.stopScanning()
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scannedArtikelId =
                bundle.getString("scannedArtikelId") ?: return@setFragmentResultListener
            handleArtikelIdSearch(scannedArtikelId)
        }
    }

    private fun handleArtikelIdSearch(artikelId: String) {
        val currentStockyardId = arguments?.getInt("stockyardId")
        val inventoryItems = inventorySharedViewModel.inventoryItemIdsAndArticleIds
        val filteredItems =
            inventoryItems?.filter { it?.warehouseStockYardId == currentStockyardId }
        val matchingItems =
            filteredItems?.filterNotNull()?.filter { it.articleId == artikelId }?.toMutableList()
        if (matchingItems != null && matchingItems.isNotEmpty()) {
            inventoryItemsAdapter.updateList(matchingItems)

            if (matchingItems.size > 1) {
                val bottomSheet = SuccessScanBottomSheet.newInstance(
                    titleText = artikelId,
                    descriptionText = getString(R.string.multiple_articles_found_would_you_like_to_select_one_from_them),
                    button1Text = getString(R.string.yes_continue),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        val foundItem = matchingItems.first()
                        val itemClickListener: (Int) -> Unit = { itemId ->
                            val bundle = Bundle().apply {
                                putInt("inventoryArticle", foundItem.id)
                                putString("articleId", foundItem.articleId)
                                putString("articleDescription", foundItem.articleDescription)
                                putInt("amount", foundItem.actualStock ?: 0)
                                putString("comment", foundItem.comment)
                                putString("matchCode", foundItem.articleMatchcode)
                                putString(
                                    "unitCode",
                                    foundItem.actualUnitCode ?: foundItem.targetUnitCode
                                    ?: foundItem.baseUnitCode
                                )
                            }
                            navigateToArticleDescriptionFragment(bundle, foundItem)
                        }
                        val inventoryItemsAdapter = InventoryItemsAdapter(
                            mutableListOf(),
                            itemClickListener,
                            inventorySharedViewModel
                        )
                        binding.inventoryList.adapter = inventoryItemsAdapter
                        binding.inventoryList.layoutManager = LinearLayoutManager(context)
                        inventoryItemsAdapter.updateList(matchingItems.toMutableList())
                    },
                    button2Callback = {
                        scannerHelper?.stopScanning()
                        val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    }
                )
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            } else {
                val foundItem = matchingItems.first()

                inventorySharedViewModel.saveClickedItemId(foundItem.id)
                inventorySharedViewModel.saveClickedItemIdActualStock(foundItem.actualStock)
                inventorySharedViewModel.savearticleId(foundItem.articleId)
                inventorySharedViewModel.saveArticleDescription(foundItem.articleDescription)
                val comment = foundItem.comment
                inventorySharedViewModel.saveClickedArticleComment(comment)
                inventorySharedViewModel.saveArticleMatchCode(foundItem.articleMatchcode)
                inventorySharedViewModel.saveUpdatedActualUnitCode(itemId = foundItem.id,
                    foundItem.actualUnitCode ?: foundItem.targetUnitCode ?: foundItem.baseUnitCode
                )

                val bottomSheet = SuccessScanBottomSheet.newInstance(
                    titleText = artikelId,
                    descriptionText = getString(R.string.match_code_found_would_like_to_continue_picking_article),
                    button1Text = getString(R.string.yes_pick_articles),
                    button2Text = getString(R.string.scan_again),
                    button1Callback = {
                        val bundle = Bundle().apply {
                            putInt("inventoryArticle", foundItem.id)
                            putString("articleId", foundItem.articleId)
                            putString("articleDescription", foundItem.articleDescription)
                            putInt("amount", foundItem.actualStock ?: 0)
                            putString("comment", foundItem.comment)
                            putString("matchCode", foundItem.articleMatchcode)
                            putString("unitCode",foundItem.actualUnitCode?:foundItem.targetUnitCode?:foundItem.baseUnitCode)
                        }
                        navigateToArticleDescriptionFragment(bundle, foundItem)
                    },
                    button2Callback = {
                        scannerHelper?.stopScanning()
                        val bottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    }
                )
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        } else {
            inventoryItemsAdapter.updateList(mutableListOf())
            val errorBottomSheet = ErrorScanBottomSheet.newInstance(
                titleText = getString(R.string.unknown_bar_code),
                descriptionText = getString(R.string.popup_description),
                buttonText = getString(R.string.scan_again),
                button1Callback = {
                    scannerHelper?.stopScanning()
                    val scanAgainBottomSheet = ScanOptionsBottomSheet.newInstance(ScanType.ARTICLE)
                    scanAgainBottomSheet.show(parentFragmentManager, scanAgainBottomSheet.tag)
                }
            )
            errorBottomSheet.show(parentFragmentManager, errorBottomSheet.tag)
        }
    }

    private fun navigateToArticleDescriptionFragment(bundle: Bundle, foundItem: InventoryItem) {
        inventorySharedViewModel.saveClickedInventoryItem(foundItem)
        findNavController().navigate(
            R.id.action_inventoryItemsFragment_to_inventoryArticleDescriptionFragment,
            bundle
        )
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
            scanEventListener = this@InventoryItemsFragment
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
            ScanType.ONLY_STOCKYARD, ScanType.STOCKYARD -> {
                inventoryItemsViewModel.onItemEvent(
                    InventoryItemsEvent.GetWarehouseStockyardById(
                        data
                    )
                )
            }

            ScanType.ARTICLE -> {
                handleArtikelIdSearch(data)
            }
        }
    }

    private fun handleArgsUpdateFromMatchFoundResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "UpdateArgsFromMatchFoundFragment", viewLifecycleOwner
        ) { _, bundle ->
            scanType = bundle.getParcelable<ScanType>("scanType")!!
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