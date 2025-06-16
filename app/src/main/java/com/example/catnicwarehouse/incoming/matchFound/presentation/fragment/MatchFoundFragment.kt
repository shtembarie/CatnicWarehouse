package com.example.catnicwarehouse.incoming.matchFound.presentation.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchFoundBinding
import com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment.DefectiveArticleBottomSheetFragment
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundEvent
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundViewState
import com.example.catnicwarehouse.incoming.matchFound.presentation.viewModel.MatchFoundViewModel
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.shared.presentation.enums.ItemType
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MatchFoundFragment : BaseFragment(),
    DefectiveArticleBottomSheetFragment.DefectiveValuesUpdateListener {

    private var _binding: FragmentMatchFoundBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val viewModel: MatchFoundViewModel by viewModels()
    private var deliveryItemId: String = ""
    private var deliveryId: String = ""

    private val args:MatchFoundFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.deliveryItemId.let {
            deliveryItemId = it
        }
        // Only when the screen opens the first time update the unit same as the selected article api response
        // Also remove any prior defective field values
        initDefaultValues()

        observeSharedEvents()
        observeMatchFoundEvents()
        handleBackPress()
        handleClickActions()
        handleAmountUpdates()
        handleDefectiveAmountUpdates()
        handleDefectiveCommentUpdates()
        handleButtonUI()
    }

    private fun handleButtonUI(){

        val outlineThemedContext =
            ContextThemeWrapper(requireContext(), R.style.OutlineDisabledButton)

        val baseThemedContext =
            ContextThemeWrapper(requireContext(), R.style.BaseDisabledButton)

        val outlineEnabledThemedContext =
            ContextThemeWrapper(requireContext(), R.style.OutlineButton)

        if(sharedViewModel.getSelectedArticleItemModel()?.status == "BKD"){
            with(binding){

                // confirm button
                confirmButton.setBackgroundResource(
                    baseThemedContext.obtainStyledAttributes(
                        R.style.BaseDisabledButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )

                confirmButton.setTextColor(requireContext().getColor(R.color.grey_400))
                confirmButton.isEnabled = false

                // put in stockyard button
                putInStockyardButton.setBackgroundResource(
                    outlineThemedContext.obtainStyledAttributes(
                        R.style.OutlineDisabledButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )

                putInStockyardButton.setTextColor(requireContext().getColor(R.color.grey_400))
                putInStockyardButton.isEnabled = false
            }
        }

        else if (deliveryItemId.isEmpty()){

            with(binding) {

                // put in stockyard button
                putInStockyardButton.setBackgroundResource(
                    outlineThemedContext.obtainStyledAttributes(
                        R.style.OutlineDisabledButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )

                putInStockyardButton.setTextColor(requireContext().getColor(R.color.grey_400))
                putInStockyardButton.isEnabled = false
            }

        }else{

            with(binding) {

                // put in stockyard button
                putInStockyardButton.setBackgroundResource(
                    outlineEnabledThemedContext.obtainStyledAttributes(
                        R.style.OutlineButton,
                        intArrayOf(android.R.attr.background)
                    ).getResourceId(0, 0)
                )

                putInStockyardButton.setTextColor(requireContext().getColor(R.color.dark_orange))
                putInStockyardButton.isEnabled = false
            }
        }
    }


    private fun handleAmountUpdates() {
        parentFragmentManager.setFragmentResultListener(
            "handleAmountAndUnitUpdate",
            viewLifecycleOwner
        ) { _, bundle ->
            val amount = bundle.getString("amount")!!
            val amountUnit = bundle.getString("amountUnit")!!
            val itemType = ItemType.valueOf(bundle.getString("itemType")!!)

            sharedViewModel.onEvents(
                SharedEvent.UpdateSelectedQty(amount),
                SharedEvent.UpdateSelectedQtyUnit(amountUnit)
            )
        }
    }


    private fun handleDefectiveAmountUpdates() {

        parentFragmentManager.setFragmentResultListener(
            "handleDefectiveAmountAndUnitUpdate",
            viewLifecycleOwner
        ) { _, bundle ->
            val amount = bundle.getString("amount")!!
            val amountUnit = bundle.getString("amountUnit")!!
            val itemType = ItemType.valueOf(bundle.getString("itemType")!!)


            val bottomSheetFragment =
                childFragmentManager.findFragmentByTag("DefectiveArticleBottomSheetFragment")
            if (bottomSheetFragment is DefectiveArticleBottomSheetFragment) {
                bottomSheetFragment.handleReceivedResultFromDefectiveQtyAndUnit(
                    amount = amount,
                    amountUnit = amountUnit,
                    itemType = itemType.name
                )
            }

        }
    }


    private fun handleDefectiveCommentUpdates() {

        parentFragmentManager.setFragmentResultListener(
            "handleDefectiveCommentUpdate",
            viewLifecycleOwner
        ) { _, bundle ->
            val comment = bundle.getString("comment")!!
            val commentType = bundle.getString("commentType")!!

            val bottomSheetFragment =
                childFragmentManager.findFragmentByTag("DefectiveArticleBottomSheetFragment")
            if (bottomSheetFragment is DefectiveArticleBottomSheetFragment) {
                bottomSheetFragment.handleReceivedResultFromDefectiveComment(
                    comment = comment,
                    commentType = commentType
                )
            }

        }
    }

    private fun initDefaultValues() {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedQtyUnit(
                selectedQuantityUnit = sharedViewModel.getSelectedQuantityUnit()
                    ?: sharedViewModel.getSelectedArticleItemModel()?.unitCode ?: ""
            ),
            SharedEvent.UpdateSelectedDefectiveUnit(
                selectedDefectiveUnit = sharedViewModel.getSelectedDefectiveUnit()
                    ?: sharedViewModel.getSelectedArticleItemModel()?.unitCode
                    ?: ""
            ),
            SharedEvent.UpdateSelectedDefectiveComment(sharedViewModel.getSelectedDefectiveComment()),
            SharedEvent.UpdateSelectedDefectiveReason(
                sharedViewModel.getSelectedDefectiveReason() ?: DefectiveReason.PHYSICAL_DAMAGE
            ),
            SharedEvent.UpdateSelectedDefectiveQty(sharedViewModel.getSelectedDefectiveQty()?:"0")
        )
    }

    private fun observeMatchFoundEvents() {
        viewModel.matchFoundFlow.onEach { state ->
            when (state) {
                is MatchFoundViewState.DeliveryItemCreated -> {
                    progressBarManager.dismiss()
                    deliveryId = state.deliveryId
                    deliveryItemId = state.deliveryItemId.toString()
                    showPositiveBanner(getString(R.string.delivery_item_created))

                    with(binding) {
                        val outlineThemedContext =
                            ContextThemeWrapper(requireContext(), R.style.OutlineButton)
                        // put in stockyard button
                        putInStockyardButton.setBackgroundResource(
                            outlineThemedContext.obtainStyledAttributes(
                                R.style.OutlineButton,
                                intArrayOf(android.R.attr.background)
                            ).getResourceId(0, 0)
                        )

                        putInStockyardButton.setTextColor(requireContext().getColor(R.color.dark_orange))
                        putInStockyardButton.isEnabled = true
                    }
                }

                MatchFoundViewState.Empty -> progressBarManager.dismiss()
                is MatchFoundViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                MatchFoundViewState.Loading -> progressBarManager.show()
                MatchFoundViewState.Reset -> progressBarManager.dismiss()
                is MatchFoundViewState.DeliveryItemBooked -> {
                    progressBarManager.dismiss()
                    findNavController().navigate(R.id.action_matchFoundFragment_to_articlesFragment)
                }

                is MatchFoundViewState.DeliveryItemUpdated -> {
                    progressBarManager.dismiss()
                    if (state.isItemUpdated == true)
                        showPositiveBanner(getString(R.string.delivery_item_udpated))
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSharedEvents() {
        sharedViewModel.incomingSharedFlow.onEach { state ->
            when (state) {
                IncomingSharedViewState.Empty -> progressBarManager.dismiss()
                is IncomingSharedViewState.Error -> progressBarManager.dismiss()
                IncomingSharedViewState.Loading -> progressBarManager.show()
                else -> {
                    initUI()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleClickActions() {
        with(binding) {
//            //warehouse stockyard section
            idSection.viewContainer.setOnClickListener {
                findNavController().clearBackStack(R.id.unloadingStockyardFragment)
                MatchFoundFragmentDirections.actionMatchFoundFragmentToUnloadingStockyardFragment(
                    ScanType.ONLY_STOCKYARD.name
                ).let { action ->
                    findNavController().navigate(action)
                }
            }

            //amount section
            quantitySection.viewContainer.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(
                        "amount",
                        sharedViewModel.getSelectedQty()
                    )
                    putString(
                        "unit",
                        sharedViewModel.getSelectedQuantityUnit() ?: ""
                    )
                    putString(
                        "itemType", ItemType.QTY.name
                    )
                }
                findNavController().navigate(R.id.amountItemFragment, bundle)
            }

            //defective section
            defectiveSection.viewContainer.setOnClickListener {
                showDefectiveArticleBottomSheet()
            }

            //confirm button to create delivery Item
            confirmButton.setOnClickListener {
                deliveryId = sharedViewModel.getDeliveryId()?: ""

                //create new delivery item if not existing already
                //else update delivery item
                if (deliveryItemId.isEmpty()) {
                    createNewDeliveryItem()
                } else {
                    updateExistingDeliveryItem()
                }
            }

            //putInStockyardButton button to put the article in the stockyard
            putInStockyardButton.setOnClickListener {
                viewModel.onEvent(
                    MatchFoundEvent.BookDeliveryItem(
                        deliveryId = sharedViewModel.getDeliveryId() ?: "",
                        deliveryItemId = deliveryItemId
                    )
                )
            }
        }
    }


    private fun updateExistingDeliveryItem() {

        val updateDeliveryItemRequestModel = UpdateDeliveryItemRequestModel(
            articleId = sharedViewModel.getSelectedArticleItemModel()?.articleId,
            unitCode = sharedViewModel.getSelectedQuantityUnit(),
            amount = sharedViewModel.getSelectedQty().toFloat(),
            defectiveAmount = sharedViewModel.getSelectedDefectiveQty()?.toInt()
                ?: 0,
            defectiveUnitCode = sharedViewModel.getSelectedDefectiveUnit(),
            reason = sharedViewModel.getSelectedDefectiveReason()?.value,
            comment =sharedViewModel.getSelectedDefectiveComment(),
            warehouseStockYardId = sharedViewModel.getSelectedWarehouseStockyardId()

        )

        viewModel.onEvent(
            MatchFoundEvent.UpdateDeliveryItem(
                deliveryId = deliveryId,
                deliveryItemId = deliveryItemId,
                updateDeliveryItemRequestModel = updateDeliveryItemRequestModel
            )
        )
    }

    private fun createNewDeliveryItem() {
        val createDeliveryItemRequestModel = CreateDeliveryItemRequestModel(
            articleId = sharedViewModel.getSelectedArticleItemModel()?.articleId,
            unitCode = sharedViewModel.getSelectedQuantityUnit(),
            amount = sharedViewModel.getSelectedQty().toFloat(),
            defectiveAmount = sharedViewModel.getSelectedDefectiveQty()?.toInt()
                ?: 0,
            defectiveUnitCode = sharedViewModel.getSelectedDefectiveUnit(),
            reason = sharedViewModel.getSelectedDefectiveReason()?.value,
            comment =sharedViewModel.getSelectedDefectiveComment(),
            warehouseStockYardId = sharedViewModel.getSelectedWarehouseStockyardId()
        )
        viewModel.onEvent(
            MatchFoundEvent.CreateDeliveryItem(
                deliveryId,
                createDeliveryItemRequestModel
            )
        )
    }


    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                passArgsToPreviousFragment()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        handleBackButton()
    }

    private fun passArgsToPreviousFragment() {
        parentFragmentManager.setFragmentResult(
            "UpdateArgsFromMatchFoundFragment",
            bundleOf(
                "scanType" to ScanType.STOCKYARD
            )
        )
    }

    private fun handleBackButton() {
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            passArgsToPreviousFragment()
            findNavController().popBackStack()
        }
    }

    private fun initUI() {


        with(binding) {
            deliveryHeader.headerTitle.text = getString(R.string.header_match_found)

            //warehouse section
            val stockyard = sharedViewModel.getSelectedWarehouseStockyardName()?:sharedViewModel.getSelectedWarehouseStockyardId().toString()
            idSection.titleId.text = stockyard
            if (stockyard.isNullOrEmpty().not() && stockyard != "0" )
                idSection.decsriptionId.visibility = View.VISIBLE
            else
                idSection.decsriptionId.visibility = View.GONE

            idSection.decsriptionId.text = getString(R.string.stockyard_name)
            idSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.hash_img
                )
            )


            //article
            val articleId = sharedViewModel.getSelectedArticleItemModel()?.articleId

            articleSection.titleId.text = articleId
            articleSection.decsriptionId.text = getString(R.string.article_number)
            articleSection.toRightBtn.visibility = View.GONE
            articleSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.hash_img
                )
            )

            //qty
            val qty =
                "${sharedViewModel.getSelectedQty()}/${sharedViewModel.getSelectedQuantityUnit()}"
            val qtySpannableString = qty.colorSubstringFromCharacter('/', Color.LTGRAY)

            quantitySection.titleId.text = qtySpannableString
            quantitySection.decsriptionId.text = getString(R.string.quantity_current_delivery)
            quantitySection.toRightBtn.visibility = View.VISIBLE
            quantitySection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.incoming_truck_img
                )
            )

            //defective articles
            val defectiveArticlesText =
                "${sharedViewModel.getSelectedDefectiveQty()}/${sharedViewModel.getSelectedDefectiveUnit()}"
            val defectiveQtySpannableString =
                defectiveArticlesText.colorSubstringFromCharacter('/', Color.LTGRAY)

            defectiveSection.titleId.text = defectiveQtySpannableString
            defectiveSection.decsriptionId.text = getString(R.string.defective_articles)
            defectiveSection.toRightBtn.visibility = View.VISIBLE
            defectiveSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.defective_img
                )
            )

        }
    }

    private fun showDefectiveArticleBottomSheet() {
        val bottomSheet = DefectiveArticleBottomSheetFragment().apply {
            listener = this@MatchFoundFragment
        }
        bottomSheet.show(childFragmentManager, "DefectiveArticleBottomSheetFragment")
    }

    private fun handleConfirmButton(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.confirmButton.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onEvent(MatchFoundEvent.Reset)
        _binding = null
    }

    override fun onDefectiveValuesUpdated(
        defectiveAmount: String?,
        defectiveAmountUnit: String?,
        defectiveComment: String?,
        defectiveReason: DefectiveReason?
    ) {
        sharedViewModel.onEvents(
            SharedEvent.UpdateSelectedDefectiveQty(defectiveAmount),
            SharedEvent.UpdateSelectedDefectiveUnit(defectiveAmountUnit),
            SharedEvent.UpdateSelectedDefectiveReason(defectiveReason?:DefectiveReason.PHYSICAL_DAMAGE),
            SharedEvent.UpdateSelectedDefectiveComment(defectiveComment)
        )
    }
}