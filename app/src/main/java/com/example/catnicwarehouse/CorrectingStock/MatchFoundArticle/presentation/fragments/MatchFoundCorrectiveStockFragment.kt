package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses.CorrectEvent
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses.CorrectInventorViewState
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.viewModel.MatchFoundCorrectiveStockViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchFoundCorrectiveStockBinding
import com.example.catnicwarehouse.movement.articles.presentation.adapter.StockyardArticlesAdapter
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
import com.example.shared.repository.correctingStock.model.CorrectInventoryItems
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.utils.ScannerHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.symbol.emdk.barcode.StatusData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.scan_options_layout.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MatchFoundCorrectiveStockFragment : BaseFragment(), ScanEventListener {
    private var _binding: FragmentMatchFoundCorrectiveStockBinding? = null
    private val binding get() = _binding!!
    private val corrStockSharedViewModel: CorrStockSharedViewModel by activityViewModels()
    private val matchFoundCorrectiveStockViewModel: MatchFoundCorrectiveStockViewModel by activityViewModels()

    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val viewModel: ArticlesViewModel by viewModels()

    private lateinit var stockyardArticlesAdapter: StockyardArticlesAdapter
    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ScanType.ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var clickedArticle: WarehouseStockyardInventoryResponseModel? = null
    private var isScanUIButtonPressed = false
    private var scanPopupFragment: ScanActiveFragment? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchFoundCorrectiveStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        getParams()
        setupClickListeners()
        observeCommentEvents()
        correctInventory()
        handleButtonActions()
        //handle the result back from scan option bottom sheet
        handleScanOptionResultBack()
    }
    private fun handleHeaderSection(){
        binding.articleHeader.headerTitle.text = corrStockSharedViewModel.articleId
        binding.articleHeader.toolbarSection.visibility = View.VISIBLE
        binding.articleHeader.leftToolbarButton.setOnClickListener {
            corrStockSharedViewModel.reset()
            findNavController().popBackStack()
        }
    }
    private fun observeCommentEvents() {
        matchFoundCorrectiveStockViewModel.matchFoundFlow.onEach { state ->
            when (state) {
                is CorrectInventorViewState.CorrectedInventorySaved -> {
                    progressBarManager.dismiss()

                }
                CorrectInventorViewState.Empty -> progressBarManager.dismiss()
                is CorrectInventorViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }
                CorrectInventorViewState.Loading -> progressBarManager.show()
                CorrectInventorViewState.Reset -> progressBarManager.dismiss()
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun getParams(){
        binding.idSection.titleId.text = corrStockSharedViewModel.matchCode
        binding.idSection.decsriptionId.apply {
            text = getString(R.string.code_match_found)
            setTextColor(ContextCompat.getColor(context, R.color.light_green))
        }
        binding.idSection.toRightBtn.visibility = View.GONE
        binding.articleSection.titleId.text = corrStockSharedViewModel.articleId
        binding.articleSection.decsriptionId.text = getString(R.string.article_number)
        binding.articleSection.toRightBtn.visibility = View.GONE
        val countedStock = binding.quantitySection.imgProduct.findViewById<ImageView>(R.id.img_product)
        countedStock.setImageResource(R.drawable.countedinstock)

        val initialAmount = corrStockSharedViewModel.amount
        binding.quantitySection.titleId.text = initialAmount.toString()

        val updatedAmount = corrStockSharedViewModel.updadeAmount
        binding.quantitySection.titleId.text = when {
            updatedAmount == null -> initialAmount.toString()
            true -> updatedAmount.toString()
            else -> "0"
        }

        binding.quantitySection.decsriptionId.text = getString(R.string.available_in_stock)

    }
    private fun correctInventory() {
        val warehouseStockYardId = corrStockSharedViewModel.stockyardId
        val entryId = corrStockSharedViewModel.entryId
        val amount = corrStockSharedViewModel.updadeAmount ?: corrStockSharedViewModel.amount
        val unitCode = corrStockSharedViewModel.unitCode // User input

        // Toggle button visibility
        if (corrStockSharedViewModel.updadeAmount != null) {
            binding.saveCorrectingStockArticleOrange.visibility = View.VISIBLE
            binding.saveCorrectingStockArticle.visibility = View.GONE
        } else {
            binding.saveCorrectingStockArticleOrange.visibility = View.GONE
            binding.saveCorrectingStockArticle.visibility = View.VISIBLE
        }

        // Listener for the primary save item
        binding.saveCorrectingStockArticle.setOnClickListener {
            if (amount != null) {
                handleInventoryCorrection(warehouseStockYardId, entryId, amount, unitCode)
            }
        }

        // Listener for the corrected Inventory
        binding.saveCorrectingStockArticleOrange.setOnClickListener {
            if (amount != null) {
                showCorrectInventoryBottomSheet(warehouseStockYardId, entryId, amount, unitCode)
            }
            corrStockSharedViewModel.isItemCorrected = true
        }
    }
    private fun handleInventoryCorrection(
        warehouseStockYardId: Int?,
        entryId: Int?,
        amount: Any,
        unitCode: String?
    ) {
        val correctInventoryItems = amount?.let {
            CorrectInventoryItems(
                amount = it,
                unitCode = unitCode
            )
        }

        correctInventoryItems?.let {
            CorrectEvent.CorrectInventoryAmountUnit(
                warehouseStockYardId = warehouseStockYardId,
                entryId = entryId,
                correctInventoryItems = it
            )
        }?.let {
            matchFoundCorrectiveStockViewModel.onEvent(
                it
            )
        }

        corrStockSharedViewModel.reset()
        progressBarManager.show()

        // Delay popBackStack by 2 seconds to fix the bug when navigation
        // back so the amount of item will have time to change and the user to see directly
        Handler(Looper.getMainLooper()).postDelayed({
            progressBarManager.dismiss()
            findNavController().popBackStack()
        }, 1000)
    }
    private fun showCorrectInventoryBottomSheet(
        warehouseStockYardId: Int?,
        entryId: Int?,
        amount: Any,
        unitCode: String?
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.correct_inventory, null)
        bottomSheetDialog.setContentView(view)

        // Making the bottom sheet expanded by default
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true // Optional: Prevent collapsing
        }

        //binding the views
        val titleTextView = view.findViewById<TextView>(R.id.title_popup_finalize)
        titleTextView.text = getString(R.string.finalize_item)
        val descriptionTextView = view.findViewById<TextView>(R.id.description_popup)
        descriptionTextView.text = getString(R.string.are_you_sure_you_want_to_correct_this_item)
        val toRightBtn = view.findViewById<ImageView>(R.id.to_right_btn)
        toRightBtn.visibility = View.GONE
        val titleLocation = view.findViewById<TextView>(R.id.title_id)
        titleLocation.text = getString(R.string.location)
        val location = view.findViewById<TextView>(R.id.decsription_id)
        val articleIdarg = corrStockSharedViewModel.articleId
        location.text = context?.getString(R.string.artikel_number_corr, articleIdarg)
        corrStockSharedViewModel.saveArticleForNewDesign(articleIdarg.toString())
        val totalAticle = view.findViewById<TextView>(R.id.total_amount_tv)
        totalAticle.text = getString(R.string.total_articles)
        val totalAmount = view.findViewById<TextView>(R.id.to_right_btn2)
        totalAmount.text = corrStockSharedViewModel.updadeAmount.toString()

        // Set up buttons in the bottom sheet
        view.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            handleInventoryCorrection(warehouseStockYardId, entryId, amount, unitCode)
            bottomSheetDialog.dismiss()
        }

        view.findViewById<TextView>(R.id.backButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
    private fun setupClickListeners() {
        binding.quantitySection.viewContainer.setOnClickListener {
            findNavController().navigate(R.id.action_matchFoundCorrectiveStockFragment_to_itemAmountFragment)
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun handleButtonActions() {
        with(binding) {
            scanStockyardsButton.setOnClickListener {

                val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)

            }
            scanStockyardsTextView.setOnClickListener {

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
            scanEventListener = this@MatchFoundCorrectiveStockFragment
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
                //viewModel.onEvent(ArticlesEvent.SearchArticle(data))
                if (data == corrStockSharedViewModel.articleId) {
                    val bottomSheet = SuccessScanBottomSheet.newInstance(
                        titleText = getString(R.string.article_match_found),
                        descriptionText = getString(R.string.this_article_number_is_matching),
                        button1Text = "Proceed",
                        button2Text = ""
                    )
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                } else {
                    showErrorScanBottomSheet()
                }
            }
        }
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
    }

}