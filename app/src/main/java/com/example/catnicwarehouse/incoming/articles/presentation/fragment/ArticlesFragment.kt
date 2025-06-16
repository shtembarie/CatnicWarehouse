package com.example.catnicwarehouse.incoming.articles.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDeliveryBinding
import com.example.catnicwarehouse.incoming.articles.data.mapArticleItemToArticleItemUI
import com.example.catnicwarehouse.incoming.articles.presentation.adapter.ArticlesAdapter
import com.example.catnicwarehouse.incoming.articles.presentation.adapter.ArticlesAdapterListInteraction
import com.example.catnicwarehouse.incoming.articles.presentation.sealedClass.ArticlesEvent
import com.example.catnicwarehouse.incoming.articles.presentation.sealedClass.ArticlesViewState
import com.example.catnicwarehouse.incoming.articles.presentation.viewModel.ArticlesViewModel
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent
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
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.networking.network.delivery.model.getDelivery.ArticleItem
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
class ArticlesFragment : BaseFragment(), ArticlesAdapterListInteraction, ScanEventListener {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private lateinit var articlesAdapter: ArticlesAdapter
    private val viewModel: ArticlesViewModel by viewModels()
    private var isThereExistingItems = false

    private var manualInputBottomSheet: ManualInputBottomSheet? = null
    private var scanType: ScanType = ARTICLE
    private var scannerHelper: ScannerHelper? = null
    private var scanPopupFragment: ScanActiveFragment? = null
    private var scannerType: ScannerType? = null
    private var isScanUIButtonPressed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        observeArticlesEvents()
        setUpAdapter()
        viewModel.onEvent(
            ArticlesEvent.GetDelivery(
                sharedViewModel.getDeliveryId() ?: ""
            )
        )
        handleDoneButtonAction()
        handleAddNewArticleButtonAction()
        handleSuccessArticleScanResultBack()
        handleScanOptionResultBack()
        observeSharedEvents()
    }

    private fun handleAddNewArticleButtonAction() {
        binding.newDeliveryView.setOnClickListener {
            openScanOptionsBottomSheet()
        }
//        binding.newDeliveryView.setOnClickListener {
//            val action =
//                ArticlesFragmentDirections.actionArticlesFragmentToUnloadingStockyardFragment(
//                    ScanType.STOCKYARD.name
//                )
//            findNavController().navigate(action)
//        }
    }

    private fun handleDoneButtonAction() {
        binding.buttonContinue.setOnClickListener {
            findNavController().navigate(R.id.action_articlesFragment_to_deliveryDetailsFragment)
        }
    }

    private fun observeArticlesEvents() {
        viewModel.articlesFlow.onEach { state ->

            when (state) {
                is ArticlesViewState.Delivery -> {
                    progressBarManager.dismiss()
                    sharedViewModel.onEvents(SharedEvent.UpdateGetDeliveryResponseModel(state.delivery))
                    articlesAdapter.submitList(state.delivery?.articleItems)
                    binding.buttonContinue.visibility = View.VISIBLE
                    binding.buttonContinue.text = getString(R.string.done)
                }

                ArticlesViewState.Empty -> progressBarManager.dismiss()
                is ArticlesViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                ArticlesViewState.Loading -> progressBarManager.show()
                ArticlesViewState.Reset -> progressBarManager.dismiss()
                is ArticlesViewState.ArticlesForDeliveryFound -> {
                    progressBarManager.dismiss()
                    if (state.articles.isNullOrEmpty()) {
                        showErrorScanBottomSheet()
                        return@onEach
                    }
                    sharedViewModel.onEvents(
                        SharedEvent.UpdateSelectedArticleItemModel(
                            SharedEvent.mapArticleItemForDeliveryToArticleItemUI(
                                state.articles[0]
                            )
                        ),
                        SharedEvent.UpdateSelectedQty(state.articles[0].quantityInPurchaseOrders.toString())
                    )

                    progressBarManager.dismiss()
                    handleSuccessArticleScanResultBackAction(scanType)
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSharedEvents() {
        sharedViewModel.incomingSharedFlow.onEach { state ->
            when (state) {

                IncomingSharedViewState.Empty -> progressBarManager.dismiss()
                is IncomingSharedViewState.Error -> progressBarManager.dismiss()
                is IncomingSharedViewState.GetDeliveryResponseModelResult -> {
                    progressBarManager.dismiss()
                    isThereExistingItems =
                        sharedViewModel.getDeliveryResponseModel()?.articleItems?.isNotEmpty() == true
                    // In case of existing items update the warehouseId Local field, in case new articles need to be added
                    //else route to the unloading stockyard screen for the complete flow
                    if (isThereExistingItems) {
                        val warehouseId =
                            sharedViewModel.getDeliveryResponseModel()?.articleItems?.get(0)?.warehouseStockYardId
                                ?: 0
                        sharedViewModel.onEvents(
                            SharedEvent.UpdateSelectedWarehouseStockyard(
                                warehouseId
                            )
                        )
                    }
                }

                IncomingSharedViewState.Loading -> progressBarManager.show()
                else -> progressBarManager.dismiss()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun handelHeaderSection() {
        with(binding) {
            deliveryHeader.headerTitle.text = getString(R.string.delivery)
            textView.text = getString(R.string.add_another_article)
            deliveryHeader.rightToolbarButton.visibility = View.GONE
        }
        handleBackPress()
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog(requireActivity(),
                    positiveClick = {
                        findNavController().popBackStack(R.id.deliveryFragment, false)
                    }
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        handleBackButton()
    }

    private fun handleBackButton() {
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            showExitDialog(requireActivity(),
                positiveClick = {
                    findNavController().popBackStack(R.id.deliveryFragment, false)
                }
            )
        }
    }

    private fun setUpAdapter() {
        articlesAdapter = ArticlesAdapter(interaction = this, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.deliveryList.layoutManager = layoutManager
        binding.deliveryList.adapter = articlesAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewClicked(articleItem: ArticleItem) {
        updateSharedViewModelOnArticleSelection(articleItem = articleItem)
        ArticlesFragmentDirections.actionArticlesFragmentToMatchFoundFragment(
            articleItem.id.toString()
        ).let { action ->
            findNavController().navigate(action)
        }

    }

    private fun updateSharedViewModelOnArticleSelection(articleItem: ArticleItem) {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedArticleItemModel(
                articleItem = mapArticleItemToArticleItemUI(
                    articleItem = articleItem
                )
            ),
            SharedEvent.UpdateSelectedQty(articleItem.amount.toString()),
            SharedEvent.UpdateSelectedQtyUnit(articleItem.unitCode),
            SharedEvent.UpdateSelectedDefectiveQty(articleItem.defectiveAmount.toString()),
            SharedEvent.UpdateSelectedDefectiveUnit(articleItem.defectiveUnit),
            SharedEvent.UpdateSelectedDefectiveReason(articleItem.defectiveReason?.let {
                DefectiveReason.fromValue(
                    it
                )
            } ?: DefectiveReason.PHYSICAL_DAMAGE),
            SharedEvent.UpdateSelectedDefectiveComment(articleItem.defectiveComment),
            SharedEvent.UpdateSelectedWarehouseStockyard(articleItem.warehouseStockYardId),
            SharedEvent.UpdateSelectedWarehouseStockyardName(
                selectedWarehouseStockyardName = articleItem.warehouseStockYardName ?: ""
            )
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

    private fun handleSuccessArticleScanResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "handleSuccessArticleScan", viewLifecycleOwner
        ) { _, bundle ->
            val scanType = bundle.getParcelable<ScanType>("scanType")!!
            handleSuccessArticleScanResultBackAction(scanType)
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
                viewModel.onEvent(ArticlesEvent.SearchArticle(data))
            }
        }
    }

    private fun openScanOptionsBottomSheet() {
        scannerHelper?.stopScanning()
        val bottomSheet = ScanOptionsBottomSheet.newInstance(scanType = scanType)
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun openManualInputBottomSheet(scanType: ScanType) {
        manualInputBottomSheet = ManualInputBottomSheet.newInstance(
            scanType = scanType,
            moduleType = ModuleType.INCOMING
        ).apply {
            onDismissListener = {
                manualInputBottomSheet = null
            }
        }
        manualInputBottomSheet?.show(parentFragmentManager, manualInputBottomSheet?.tag)
    }

    private fun showErrorScanBottomSheet() {
        val bottomSheet = ErrorScanBottomSheet.newInstance()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun handleSuccessArticleScanResultBackAction(scanType: ScanType) {
        if (sharedViewModel.getArticleItemModelListFromSearchedArticle().isNullOrEmpty().not()) {
            findNavController().navigate(R.id.action_articlesFragment_to_articleSelectionFragment3)
        } else {
            findNavController().navigate(R.id.action_articlesFragment_to_unloadingStockyardFragment)
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