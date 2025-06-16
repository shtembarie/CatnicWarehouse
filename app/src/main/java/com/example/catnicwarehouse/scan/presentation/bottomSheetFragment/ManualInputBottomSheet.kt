package com.example.catnicwarehouse.scan.presentation.bottomSheetFragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.databinding.FragmentManualInput2Binding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.scan.presentation.adapter.ArticleAutoCompleteAdapter
import com.example.catnicwarehouse.scan.presentation.adapter.HierarchyAutoCompleteAdapter
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.enums.ScanType.*
import com.example.catnicwarehouse.scan.presentation.helper.OnItemSelectListener
import com.example.catnicwarehouse.scan.presentation.helper.getFullPath
import com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput.ManualInputEvent
import com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput.ManualInputViewState
import com.example.catnicwarehouse.scan.presentation.viewModel.ManualInputViewModel
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.CHECKS
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.CORRECTIVE_STOCK
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.DEFECTIVE_ITEMS
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.INCOMING
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.INVENTORY
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.MOVEMENTS
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.PACKING_1
import com.example.catnicwarehouse.shared.presentation.enums.ModuleType.PACKING_2
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ManualInputBottomSheet : BottomSheetDialogFragment(),
    OnItemSelectListener {


    companion object {
        private const val ARG_SCAN_TYPE = "scan_type"
        private const val ARG_MODULE_TYPE = "module_type"

        fun newInstance(scanType: ScanType, moduleType: ModuleType): ManualInputBottomSheet {
            return ManualInputBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCAN_TYPE, scanType)
                    putParcelable(ARG_MODULE_TYPE, moduleType)
                }
            }
        }
    }

    var onDismissListener: (() -> Unit)? = null

    private var _binding: FragmentManualInput2Binding? = null
    private val binding get() = _binding!!
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }
    private val incomingSharedViewModel: SharedViewModelNew by activityViewModels()
    private val manualInputViewModel: ManualInputViewModel by viewModels()
    private var observeSharedEventsJob: Job? = null
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()
    private var warehouseStockyards: List<WarehouseStockyardsDTO>? = null
    private var selectedWarehouseStockyard: WarehouseStockyardsDTO? = null
    private var selectedArticle: ArticlesForDeliveryResponseDTO? = null
    private lateinit var textWatcher: TextWatcher
    private var isProgrammaticUpdate = false
    private var allStockyards: Map<Int, WarehouseStockyardsDTO?>? = null


    private lateinit var scanType: ScanType
    private lateinit var moduleType: ModuleType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scanType = it.getParcelable(ARG_SCAN_TYPE)
                ?: throw IllegalArgumentException("ScanType is missing")
            moduleType = it.getParcelable(ARG_MODULE_TYPE)
                ?: throw IllegalArgumentException("ModuleType is missing")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualInput2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSharedEvents()
        handleConfirmButtonBasedOnScanType(scanType = scanType)
        updateUIBasedOnScanType(scanType = scanType)
        observeManualInputResponse()
    }

    private fun observeSharedEvents() {
        observeSharedEventsJob = incomingSharedViewModel.incomingSharedFlow.onEach { state ->
            when (state) {
                IncomingSharedViewState.Empty -> progressBarManager.dismiss()
                is IncomingSharedViewState.Error -> progressBarManager.dismiss()
                IncomingSharedViewState.Loading -> progressBarManager.show()
                else -> progressBarManager.dismiss()
            }
        }.launchIn(lifecycleScope)
    }

    private fun observeManualInputResponse() {
        manualInputViewModel.manualInputFlow.onEach { state ->
            when (state) {
                is ManualInputViewState.ArticlesForDeliveryFound -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    //In case of Incoming
                    if (moduleType == INCOMING
                        || moduleType == INVENTORY
                    ) {

                        //In case of only one article
                        if (state.articles.size == 1) {
                            if (moduleType == INCOMING) {
                                incomingSharedViewModel.onEvents(
                                    SharedEvent.UpdateSelectedArticleItemModel(
                                        SharedEvent.mapArticleItemForDeliveryToArticleItemUI(
                                            state.articles[0]
                                        )
                                    ),
                                    SharedEvent.UpdateSelectedQty(state.articles[0].quantityInPurchaseOrders.toString())
                                )
                            } else { // INVENTORY
                                inventorySharedViewModel.scannedArticle = state.articles[0]

                            }

                        }
                        //In case of multiple articles with the same id
                        else if (state.articles.size > 1) {
                            if (moduleType == INCOMING) {
                                incomingSharedViewModel.onEvents(
                                    SharedEvent.UpdateArticleItemModelListFromSearchedArticle(
                                        state.articles
                                    )
                                )
                            }else{
                                openArticleSelectionBottomSheet(state.articles)
                            }

                        }
                        parentFragmentManager.setFragmentResult(
                            "handleSuccessArticleScan",
                            bundleOf(
                                "scanType" to scanType,
                                "scannedArtikelId" to selectedArticle?.articleId
                            )
                        )
                        dismiss()
                    } else {
                        val validArticles = getValidArticles(state.articles)
                        if (validArticles.count() > 1) {
                            openArticleSelectionBottomSheet(validArticles)
                        } else if (validArticles.count() == 1) {
                            when (moduleType) {
                                MOVEMENTS -> {
                                    movementSharedViewModel.scannedArticle = validArticles[0]
                                }

                                PACKING_1 -> {
                                    packingSharedViewModel.scannedArticle = validArticles[0]
                                }

                                DEFECTIVE_ITEMS -> {
                                    defectiveArticleSharedViewModel.scannedArticle =
                                        validArticles[0]
                                }

                                CHECKS -> {
                                    checksSharedViewModel.scannedArticle = validArticles[0]
                                }

                                INCOMING -> {}
                                INVENTORY -> {}
                                PACKING_2 -> {}
                                CORRECTIVE_STOCK -> {}
                            }
                            parentFragmentManager.setFragmentResult(
                                "handleSuccessArticleScan",
                                bundleOf(
                                    "scanType" to ARTICLE,
                                    "scannedArtikelId" to validArticles[0].articleId
                                )
                            )
                        } else {
                            showErrorScanBottomSheet()
                        }

                        dismiss()

                    }
//                    else {
//                        setupArticleAutoComplete(state.articles)
//                    }
                }

                ManualInputViewState.Empty -> progressBarManager.dismiss()
                is ManualInputViewState.Error -> {
                    progressBarManager.dismiss()
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
                    showErrorScanBottomSheet()
                }

                ManualInputViewState.Loading -> {
                    progressBarManager.show()
                }

                ManualInputViewState.Reset -> progressBarManager.dismiss()

                is ManualInputViewState.WarehouseStockByIdFound -> {
                    progressBarManager.dismiss()
                    if (state.warehouseStockyard == null) {
                        showErrorScanBottomSheet()
                        return@onEach
                    } else {

                        //In case of Incoming
                        if (moduleType == INCOMING) {
                            incomingSharedViewModel.onEvents(
                                SharedEvent.UpdateSelectedWarehouseStockyard(
                                    state.warehouseStockyard.id
                                ),
                                SharedEvent.UpdateSelectedWarehouseStockyardName(
                                    selectedWarehouseStockyardName = state.warehouseStockyard.name
                                )
                            )
                        } //In case of Movements
                        else if (moduleType == MOVEMENTS) {
                            movementSharedViewModel.scannedStockyard = state.warehouseStockyard
                        }

                        //In case of Inventory
                        else if (moduleType == INVENTORY) {
                            inventorySharedViewModel.scannedStockyard =
                                state.warehouseStockyard
                        }


                        //In case of Packing
                        else if (moduleType == PACKING_1) {
                            packingSharedViewModel.scannedStockyard = state.warehouseStockyard
                        }
                        //In case of packing drop zone
                        else if (moduleType == PACKING_2) {
                            packingSharedViewModel.dropzoneScannedStockyardId =
                                state.warehouseStockyard.id
                        }
                        //In case of checks
                        else if (moduleType == CHECKS) {
                            checksSharedViewModel.scannedStockyard = state.warehouseStockyard
                        }


                        val fullPath = state.warehouseStockyard.getFullPath()

                        // Set result to trigger the callback in parent fragment
                        parentFragmentManager.setFragmentResult(

                            "handleSuccessStockyardScan",
                            bundleOf(
                                "titleText" to "$${state.warehouseStockyard.name}",
                                "stockyardId" to state.warehouseStockyard.id,
                                "scanType" to scanType
                            )
                        )
                        dismiss()
                    }


                }

                is ManualInputViewState.WarehouseStockyardsFound -> {
                    progressBarManager.dismiss()
                    warehouseStockyards = state.warehouseStockyards
                    if (state.isFromUserSearch) {
                        if (!warehouseStockyards.isNullOrEmpty()) {
                            selectedWarehouseStockyard =
                                warehouseStockyards?.firstOrNull { s -> s.name == binding.codeStockyardText.text.trim() }
                            if (selectedWarehouseStockyard == null) {
                                showErrorScanBottomSheet()
                                return@onEach
                            }
                            selectedWarehouseStockyard?.let {
                                parentFragmentManager.setFragmentResult(
                                    "handleSuccessStockyardScan",
                                    bundleOf(
                                        "titleText" to "${it.name}",
                                        "stockyardId" to selectedWarehouseStockyard?.id,
                                        "scanType" to scanType
                                    )
                                )
                            }
                        } else {
                            showErrorScanBottomSheet()
                            return@onEach
                        }
                    }
                    warehouseStockyards?.let { setupAutocompleteSearch(it) }
                }

                is ManualInputViewState.WarehouseStockyardInventoryEntriesResponse -> TODO()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleConfirmButtonBasedOnScanType(scanType: ScanType) {

        binding.buttonConfirm.setOnClickListener {

            hideKeyboard(binding.buttonConfirm)

            when (scanType) {
                ONLY_STOCKYARD, STOCKYARD -> {
                    // In case of stockyard check if any stockyard is already selected from the list,
                    // if not then call the search api based on user input and check if the stockyard is available or not
                    if (warehouseStockyards.isNullOrEmpty()) {
                        if (binding.codeStockyardText.text.trim().isEmpty()) {
                            showErrorBanner(
                                getString(
                                    R.string.stockyard_emty_name_search_field
                                )
                            )
                            return@setOnClickListener
                        } else if (binding.codeStockyardText.text.trim().isNotEmpty()) {
                            manualInputViewModel.onEvent(
                                ManualInputEvent.SearchWarehouseStockyards(
                                    searchTerm = binding.codeStockyardText.text.toString(),
                                    isFromUserSearch = true,
                                    warehouseCode = IncomingConstants.WarehouseParam
                                )
                            )
                            return@setOnClickListener
                        }
                        return@setOnClickListener
                    } else {
                        if (selectedWarehouseStockyard == null && binding.codeStockyardText.text.trim()
                                .isNotEmpty()
                        ) {
                            manualInputViewModel.onEvent(
                                ManualInputEvent.SearchWarehouseStockyards(
                                    searchTerm = binding.codeStockyardText.text.toString(),
                                    isFromUserSearch = true,
                                    warehouseCode = IncomingConstants.WarehouseParam
                                )
                            )
                            return@setOnClickListener
                        }
                    }

                    // Set result to trigger the callback in parent fragment
                    selectedWarehouseStockyard?.let {

                        parentFragmentManager.setFragmentResult(
                            "handleSuccessStockyardScan",
                            bundleOf(
                                "titleText" to "${it.name}",
                                "stockyardId" to it.id,
                                "parentStockYardId" to it.findTopMostParent(allStockyards).id,
                                "scanType" to scanType

                            )
                        )
                    }
                    dismiss()

                }

                ARTICLE -> {
                    updateArticleAndDismiss()
                }
            }
        }
    }

    private fun updateArticleAndDismiss() {
        val inputCode = binding.codeArticleText.text.toString().trim()
        if (inputCode.isEmpty()) {
            showErrorBanner(
                getString(
                    R.string.emty_bar_code_search_field
                )
            )
            return
        }

        // Handle the selection
//        if (moduleType == ModuleType.INCOMING) {
        manualInputViewModel.onEvent(
            ManualInputEvent.SearchArticle(
                binding.codeArticleText.text.toString().trim()
            )
        )
        return
//        }

//        parentFragmentManager.setFragmentResult(
//            "handleSuccessArticleScan",
//            bundleOf(
//                "scanType" to scanType,
//                "scannedArtikelId" to selectedArticle?.articleId
//            )
//        )
//        dismiss()

    }

    private fun getValidArticles(articles: List<ArticlesForDeliveryResponseDTO>): List<ArticlesForDeliveryResponseDTO> {
        val filteredList =
            if (moduleType == ModuleType.MOVEMENTS && movementSharedViewModel.movementItemsList != null) {
//                val validArticleIds =
//                    movementSharedViewModel.movementItemsList!!.map { it.articleId }.toSet()
//                articles.filter { it.articleId in validArticleIds }
                articles
            } else if ((moduleType == ModuleType.PACKING_1) && packingSharedViewModel.packingItems != null) {
                val validArticleIds =
                    packingSharedViewModel.packingItems!!.map { it.articleId }.toSet()
                articles.filter { it.articleId in validArticleIds }
            }
//            else if ((moduleType == ModuleType.INVENTORY && inventorySharedViewModel.filteredInventoryItems != null)) {
//                val validArticleIds =
//                    inventorySharedViewModel.filteredInventoryItems!!.map { it.articleId }
//                        .toSet()
//                articles
//                    .filter { it.articleId in validArticleIds }
//            }
            else {
                articles
            }
        return filteredList
    }


//    private fun setupArticleAutoComplete(articles: List<ArticlesForDeliveryResponseDTO>) {
//        binding.codeArticleText.threshold = 1
//
//        val filteredArticles =
//            if (moduleType == ModuleType.MOVEMENTS && movementSharedViewModel.movementItemsList != null) {
//                val validArticleIds =
//                    movementSharedViewModel.movementItemsList!!.map { it.articleId }.toSet()
//                articles.filter { it.articleId in validArticleIds }
//            } else if ((moduleType == ModuleType.PACKING_1) && packingSharedViewModel.packingItems != null) {
//                val validArticleIds =
//                    packingSharedViewModel.packingItems!!.map { it.articleId }.toSet()
//                articles.filter { it.articleId in validArticleIds }
//            } else if ((moduleType == ModuleType.INVENTORY && inventorySharedViewModel.inventoryItemIdsAndArticleIds != null)) {
//                val validArticleIds =
//                    inventorySharedViewModel.inventoryItemIdsAndArticleIds!!.map { it?.articleId }
//                        .toSet()
//                articles.filter { it.articleId in validArticleIds }
//            } else {
//                articles
//            }
//
//        val adapter = ArticleAutoCompleteAdapter(
//            requireContext(),
//            filteredArticles
//        )
//        binding.codeArticleText.setAdapter(adapter)
//        binding.codeArticleText.showDropDown()
//        // Initialize the flag
//        isProgrammaticUpdate = false
//
//        binding.codeArticleText.setOnItemClickListener { parent, _, position, _ ->
//
//            // Temporarily remove the TextWatcher
//            isProgrammaticUpdate = true
//            binding.codeArticleText.removeTextChangedListener(textWatcher)
//            selectedArticle =
//                parent.getItemAtPosition(position) as ArticlesForDeliveryResponseDTO
//            // Set the selected article text
//            binding.codeArticleText.setText(selectedArticle!!.articleId, false)
//
//            // Re-add the TextWatcher
//            binding.codeArticleText.addTextChangedListener(textWatcher)
//            isProgrammaticUpdate = false
//
//            progressBarManager.dismiss()
//            updateArticleAndDismiss()
//        }
//
//    }


    private fun updateUIBasedOnScanType(scanType: ScanType) {
        val context = requireContext()
        val typeface = ResourcesCompat.getFont(context, R.font.robotoregular)
        binding.articleCodeTextInput.typeface = typeface

        val subtext: Pair<String, String> = when (scanType) {
            ONLY_STOCKYARD, STOCKYARD -> Pair("name", "stockyard")
            ARTICLE -> Pair("barcode", "article")
        }
        when (scanType) {
            ONLY_STOCKYARD, STOCKYARD -> {
                binding.articleCodeTextInput.visibility = View.GONE
                binding.stockyardCodeTextInput.visibility = View.VISIBLE
                manualInputViewModel.onEvent(
                    ManualInputEvent.SearchWarehouseStockyards(
                        isFromUserSearch = false,
                        warehouseCode = IncomingConstants.WarehouseParam
                    )
                )
            }

            ARTICLE -> {
                binding.articleCodeTextInput.visibility = View.VISIBLE
                binding.stockyardCodeTextInput.visibility = View.GONE


//                binding.codeArticleText.setOnEditorActionListener { _, actionId, _ ->
//                    if (actionId == EditorInfo.IME_ACTION_DONE) {
//                        hideKeyboard(binding.codeArticleText)
//                        binding.codeArticleText.clearFocus()
//                        if (moduleType != ModuleType.INCOMING)
//                            manualInputViewModel.onEvent(
//                                ManualInputEvent.SearchArticle(
//                                    binding.codeArticleText.text.toString().trim()
//                                )
//                            )
//                        true
//                    } else {
//                        false
//                    }
//                }

//                if (moduleType != ModuleType.INCOMING) {
//                    val handler = Handler(Looper.getMainLooper())
//                    var workRunnable: Runnable? = null
//
//                    textWatcher = object : TextWatcher {
//                        override fun beforeTextChanged(
//                            s: CharSequence?,
//                            start: Int,
//                            count: Int,
//                            after: Int
//                        ) {
//                        }
//
//                        override fun onTextChanged(
//                            s: CharSequence?,
//                            start: Int,
//                            before: Int,
//                            count: Int
//                        ) {
//                            if (!isProgrammaticUpdate) {
//                                // Cancel the previous runnable if the user continues typing
//                                workRunnable?.let { handler.removeCallbacks(it) }
//                                // Schedule a new runnable with a 2-second delay
//                                workRunnable = Runnable {
//                                    if (!s.isNullOrEmpty() && s.length >= 3) {
//                                        manualInputViewModel.onEvent(
//                                            ManualInputEvent.SearchArticle(
//                                                s.toString()
//                                            )
//                                        )
//                                    }
//                                }
//                                handler.postDelayed(workRunnable!!, 1500)
//                            }
//                        }
//
//                        override fun afterTextChanged(s: Editable?) {}
//                    }
//                    binding.codeArticleText.addTextChangedListener(textWatcher)
//                }
            }
        }

        binding.subText.text =
            getString(R.string.enter_the_barcode_of_the_product, subtext.first, subtext.second)
    }


    private fun showErrorBanner(message: String, displayDuration: Long = 2000) {

        BannerBar.build(requireActivity()).setTitle(message).setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.red).setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.65).toInt()
        }
    }

    private fun hideKeyboard(view: View?) {
        view?.let { v ->
            val imm =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        observeSharedEventsJob?.cancel()
        onDismissListener?.invoke()
    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun buildHierarchy(items: List<WarehouseStockyardsDTO>): List<WarehouseStockyardsDTO> {
        // Sort items by hierarchyLevel to ensure correct processing order
        val sortedItems = items.sortedBy { it.hierarchyLevel }

        // Create a map to hold each item and its corresponding children
        val itemMap = mutableMapOf<Int, WarehouseStockyardsDTO>()

        // Prepare a list for root items (those with no parentStockId)
        val rootItems = mutableListOf<WarehouseStockyardsDTO>()

        // First, populate the itemMap with all items
        sortedItems.forEach { item ->
            itemMap[item.id] =
                item.copy(children = mutableListOf()) // Make a copy with initialized children
        }

        // Now, assign children to their respective parents
        sortedItems.forEach { item ->
            val currentItem = itemMap[item.id]!!
            val parent = itemMap[item.parentStockId]
            if (parent != null) {
                // Add this item as a child to its parent
                itemMap[item.id]!!.parent = parent
                parent.children.add(itemMap[item.id]!!)

            } else {
                // If no parentStockId, it is a root item
                rootItems.add(itemMap[item.id]!!)
            }
        }

        return rootItems
    }


    private fun setupAutocompleteSearch(items: List<WarehouseStockyardsDTO>) {
        val hierarchicalItems = buildHierarchy(items)
        allStockyards = items.associateBy { it.id }

        binding.codeStockyardText.threshold = 1
        val autoCompleteAdapter =
            HierarchyAutoCompleteAdapter(requireContext(), hierarchicalItems, this)
        binding.codeStockyardText.setAdapter(autoCompleteAdapter)

        binding.codeStockyardText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(binding.codeStockyardText)  // Close the keyboard
                binding.codeStockyardText.clearFocus()    // Remove focus from the input field
                true  // Return true to indicate the action was handled
            } else {
                false  // Return false for any other actions
            }
        }


    }


    override fun onItemSelected(item: WarehouseStockyardsDTO) {
//        adapter.updateWithSearchResults(item) // Display item and its children in RecyclerView
        binding.codeStockyardText.setText(
            "${item.name} (${item.id})",
            false
        ) // Set selected item in AutoCompleteTextView
        binding.codeStockyardText.dismissDropDown() // Dismiss the dropdown

        selectedWarehouseStockyard = item

        // Case: INCOMING
        if (moduleType == ModuleType.INCOMING) {
            incomingSharedViewModel.onEvents(
                SharedEvent.UpdateSelectedWarehouseStockyard(
                    selectedWarehouseStockyard?.id!!
                ),
                SharedEvent.UpdateSelectedWarehouseStockyardName(
                    selectedWarehouseStockyardName = selectedWarehouseStockyard?.name!!
                )
            )
        }
        // Case: MOVEMENTS
        else if (moduleType == ModuleType.MOVEMENTS) {
            movementSharedViewModel.scannedStockyard = selectedWarehouseStockyard
        }

        //Case: CHECKS
        else if (moduleType == ModuleType.CHECKS) {
            checksSharedViewModel.scannedStockyard = selectedWarehouseStockyard
        }

        //Case: INVENTORY
        else if (moduleType == ModuleType.INVENTORY) {
            inventorySharedViewModel.scannedStockyard = selectedWarehouseStockyard
        }


        selectedWarehouseStockyard?.let {

            parentFragmentManager.setFragmentResult(
                "handleSuccessStockyardScan",
                bundleOf(
                    "titleText" to "${it.name}",
                    "stockyardId" to it.id,
                    "parentStockYardId" to it.findTopMostParent(allStockyards).id,
                    "scanType" to scanType

                )
            )
        }
        dismiss()


    }

    fun WarehouseStockyardsDTO.findTopMostParent(allStockyards: Map<Int, WarehouseStockyardsDTO?>?): WarehouseStockyardsDTO {
        var currentNode: WarehouseStockyardsDTO? = this
        if (allStockyards == null)
            return this

        while (currentNode?.parentStockId != null) {
            currentNode = allStockyards[currentNode.parentStockId]
        }

        return currentNode ?: this // Return itself if no parent is found
    }


    private fun openArticleSelectionBottomSheet(articlesList: List<ArticlesForDeliveryResponseDTO>?) {

        val moduleType = moduleType

        // 1) Create the bottom sheet instance with arguments
        val bottomSheet = ArticleSelectionListBottomSheet.newInstance(articlesList, moduleType)

        // 2) Show the bottom sheet
        bottomSheet.show(parentFragmentManager, "ArticleSelectionListBottomSheet")
    }


}