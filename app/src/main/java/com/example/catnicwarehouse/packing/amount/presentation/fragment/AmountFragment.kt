package com.example.catnicwarehouse.packing.amount.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAmount2Binding
import com.example.catnicwarehouse.incoming.matchFound.presentation.fragment.MatchFoundFragmentArgs
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation.CancelledPackingListBottomSheet
import com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation.DropZoneBottomSheet
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesViewState
import com.example.catnicwarehouse.packing.matchFound.presentation.viewModel.PackingArticlesViewModel
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.catnicwarehouse.packing.shared.PackingItemStatus
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_delivery_section.amount
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AmountFragment : BaseFragment(),
    CancelledPackingListBottomSheet.CancelledPackingListListener {


    private var _binding: FragmentAmount2Binding? = null
    private val binding get() = _binding!!
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val viewModel: PackingArticlesViewModel by viewModels()

    private val args: AmountFragmentArgs by navArgs()
    var isForUpdating = false
    var amountToPack = "0"
    var isPickAmountApiCalled = false
    lateinit var cancelledPackingListBottomSheet: CancelledPackingListBottomSheet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAmount2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isForUpdating = args.isUpdatingAmount
        binding.articleCodeTextInput.hint = getString(R.string.amount_of_items_to_check)
        handleHeaderSection()
        populateUIWithData()
        handleAmountSaveButtonAction()
        observePackingArticlesEvents()
    }

    private fun handleAmountSaveButtonAction() {
        binding.saveAmountButton.setOnClickListener {
            updateAmountToPack()
        }
    }

    private fun updateAmountToPack() {
        var amountToPack = binding.amountItemText.text.toString().trim().filter { it.isDigit() }
        if (amountToPack.isEmpty())
            amountToPack = "0"

        if (!isDataValid()) {
            showErrorBanner(getString(R.string.cannot_pick_up_more_than_available_items))
            return
        }
        isPickAmountApiCalled = true
        this.amountToPack = amountToPack

        if (packingSharedViewModel.selectedStockyardIdInventoryEntry?.id == null) {
            viewModel.onEvent(
                PackingArticlesEvent.GetWarehouseStockyardInventoryEntries(
                    articleId = packingSharedViewModel.selectedArticle?.articleId,
                    stockyardId = "",
                    warehouseCode = IncomingConstants.WarehouseParam,
                    isFromUserEntry = false
                )
            )
        } else {

            val packingListId = packingSharedViewModel.selectedPackingItemToPack?.packingListId
            val itemId = packingSharedViewModel.selectedPackingItemToPack?.lgid
            val pickAmountRequestModel = PickAmountRequestModel(
                packedAmount = amountToPack.toInt(),
                unitCode = packingSharedViewModel.selectedPackingItemToPack?.unitCode,
                warehouseStockYardInventoryId = packingSharedViewModel.selectedStockyardIdInventoryEntry?.id
                    ?: 0
            )

            viewModel.onEvent(
                PackingArticlesEvent.PickAmount(
                    packingListId, itemId, pickAmountRequestModel
                )
            )
        }


    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.amount_of_items)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateUIWithData() {
        binding.amountItemText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.amountItemText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.articleCodeTextInput.isEndIconVisible = false

        var valueWithUnit: SpannableString? = null
        var value: String? = null

        val amountAvailable = "${
            ((packingSharedViewModel.selectedPackingItemToPack?.amount?.toInt() ?: 0).minus(
                packingSharedViewModel.selectedPackingItemToPack?.packedAmount?.toInt() ?: 0
            ))
        }/${packingSharedViewModel.selectedPackingItemToPack?.unitCode ?: ""}"


        val amountInCaseOfEdit = "${
            packingSharedViewModel.selectedItemForPacking?.amount ?: 0
        }/${packingSharedViewModel.selectedItemForPacking?.unitCode ?: ""}"

        val dropOffAmountSpannableString =
            if (isForUpdating) amountInCaseOfEdit.colorSubstringFromCharacter(
                '/',
                Color.LTGRAY
            ) else amountAvailable.colorSubstringFromCharacter('/', Color.LTGRAY)
        valueWithUnit = dropOffAmountSpannableString

        binding.amountItemText.setText(valueWithUnit)
        // Add focus change listener
        binding.amountItemText.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // Clear the text when EditText is focused
                    if (value?.toInt() == 0)
                        binding.amountItemText.setText("")
                    else
                        binding.amountItemText.setText("${(value?.toInt()?:"0")}")

                } else {
                    // Restore the text when focus is lost
                    binding.amountItemText.setText(valueWithUnit)

                }
            }
        value =
            if (isForUpdating) "${packingSharedViewModel.selectedItemForPacking?.amount?.toInt() ?: 0}" else "${
                (packingSharedViewModel.selectedPackingItemToPack?.amount?.toInt() ?: 0).minus(
                    packingSharedViewModel.selectedPackingItemToPack?.packedAmount?.toInt() ?: 0

                )
            }"
    }

    private fun observePackingArticlesEvents() {
        viewModel.packingArticlesFlow.onEach { state ->
            when (state) {
                PackingArticlesViewState.Empty -> progressBarManager.dismiss()
                is PackingArticlesViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                    //Check for Packing List Status in case of error from Pick Amount Api
                    if (isPickAmountApiCalled) {
                        packingSharedViewModel.selectedPackingItemToPack?.packingListId?.let {
                            isPickAmountApiCalled = false
                            viewModel.onEvent(PackingArticlesEvent.GetPackingListStatus(it))
                        }
                    }
                }

                PackingArticlesViewState.Loading -> progressBarManager.show()
                is PackingArticlesViewState.PickAmountResult -> {
                    progressBarManager.dismiss()
                    if (state.isAmountPicked == true) {
                        packingSharedViewModel.selectedPackingItemToPack?.amountToPack =
                            amountToPack.toInt()
                        navigateToMatchFoundFragment()
                    }
                }

                PackingArticlesViewState.Reset -> progressBarManager.dismiss()
                is PackingArticlesViewState.ChangePackedAmountResult -> {
                    progressBarManager.dismiss()
                    if (state.isAmountUpdated == true) {
                        packingSharedViewModel.selectedPackingItemToPack?.amountToPack =
                            amountToPack.toInt()
                        navigateToMatchFoundFragment()
                    }
                }

                is PackingArticlesViewState.GetPackingListStatusResponse -> {
                    progressBarManager.dismiss()
                    //In case cancelled/cancellation requested, show the bottom sheet
                    if (state.packingListStatus?.status == PackingItemStatus.CNC.name
                        || state.packingListStatus?.status == PackingItemStatus.CNR.name
                    ) {
                        showErrorBanner("Packing List: ${PackingItemStatus.valueOf(state.packingListStatus.status)}")
                        openCancelledPackingListBottomSheet()
                    }
                }

                is PackingArticlesViewState.GetCancelPackingListResult -> {
                    progressBarManager.dismiss()
                    findNavController().popBackStack(R.id.packing_list, false)
                }

                is PackingArticlesViewState.GetDefaultPackingZonesResult -> {
                    progressBarManager.dismiss()
                    state.defaultPackingZones?.let {
                        openDefaultDropOffZoneBottomSheet(it)
                        cancelledPackingListBottomSheet.dismiss()
                    }
                }

                is PackingArticlesViewState.WarehouseStockyardInventoryEntriesResponse -> {
                    progressBarManager.dismiss()
                    val warehouseStockYardInventoryId =
                        state.warehouseStockyardInventoryEntriesResponse?.firstOrNull { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }?.id


                    val packingListId =
                        packingSharedViewModel.selectedPackingItemToPack?.packingListId
                    val itemId = packingSharedViewModel.selectedPackingItemToPack?.lgid

                    val pickAmountRequestModel = PickAmountRequestModel(
                        packedAmount = amountToPack.toInt(),
                        unitCode = packingSharedViewModel.selectedPackingItemToPack?.unitCode,
                        warehouseStockYardInventoryId = warehouseStockYardInventoryId ?: 0
                    )

                    viewModel.onEvent(
                        PackingArticlesEvent.PickAmount(
                            packingListId, itemId, pickAmountRequestModel
                        )
                    )
                }

                else -> {
                    progressBarManager.dismiss()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToMatchFoundFragment() {
        findNavController().popBackStack(R.id.matchFoundFragment2, false)
    }

    private fun openCancelledPackingListBottomSheet() {
        cancelledPackingListBottomSheet = CancelledPackingListBottomSheet.newInstance(this)
        cancelledPackingListBottomSheet.show(
            parentFragmentManager,
            "CancelledPackingListBottomSheet"
        )
    }

    private fun openDefaultDropOffZoneBottomSheet(defaultPackingZones: List<DefaultPackingZoneResultModel>) {
        val bottomSheet = DropZoneBottomSheet(
            defaultPackingZones = defaultPackingZones,
        ) { selectedZone ->
            if (selectedZone != null) {
                viewModel.onEvent(
                    PackingArticlesEvent.CancelPackingList(
                        (packingSharedViewModel.selectedAssignedPackingListItem?.id
                            ?: packingSharedViewModel.selectedSearchedPackingListId),
                        CancelPackingRequestModel(selectedZone.id ?: 0)
                    )
                )
            }
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)

    }

    override fun onChoosePackingZoneButtonFromCancelledPackingListClicked() {
        viewModel.onEvent(PackingArticlesEvent.GetDefaultPackingZones)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(PackingArticlesEvent.Reset)
    }

    private fun isDataValid(): Boolean {
        return ((packingSharedViewModel.selectedPackingItemToPack?.amount ?: 0) >
                ((packingSharedViewModel.selectedPackingItemToPack?.packedAmount ?: 0)
                        + (packingSharedViewModel.selectedPackingItemToPack?.amountToPack ?: 0)))

    }


}