package com.example.catnicwarehouse.packing.shippingContainer.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAmount2Binding
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.packing.shippingContainer.presentation.bottomSheet.ShippingContainerTypeBottomSheet
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainerViewState
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainersEvent
import com.example.catnicwarehouse.packing.shippingContainer.presentation.viewModel.ShippingContainersViewModel
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingItem.PackingItem
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.Data
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AmountFragment : BaseFragment() {

    private var _binding: FragmentAmount2Binding? = null
    private val binding get() = _binding!!
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val viewModel: ShippingContainersViewModel by viewModels()

    private var packingListId: String? = null
    private var selectedShippingContainer: Data? = null

    private val args: AmountFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmount2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        packingListId = packingSharedViewModel.selectedPackingListItem?.id
        selectedShippingContainer = args.selectedShippingContainer
        handleHeaderSection()
        showInformationBanner(
            message = getString(R.string.amount_of_items_to_be_allocated_to_the_shipping_container),
            color = R.color.holo_blue
        )
        populateUIWithData()
        observeShippingContainersEvent()
        handleSaveButtonAction()
    }

    private fun handleSaveButtonAction() {
        binding.saveAmountButton.setOnClickListener {

            if (!isDataValid()) {
                showErrorBanner(getString(R.string.cannot_pick_up_more_than_available_items))
                return@setOnClickListener
            }

            viewModel.onEvent(
                ShippingContainersEvent.GetShippingContainerPackingListItemsByShippingContainer(
                    packingListId = packingListId,
                    shippingContainerId = selectedShippingContainer?.Id
                )
            )
        }
    }


    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.amount_of_items)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateUIWithData() {
        with(binding)
        {
            articleCodeTextInput.hint = getString(R.string.amount_of_items_with_astrisk)
            amountItemText.setText(amountOfItemsPacked().toString())

            // Update button UI initially
            handleButtonUI(buttonEnabled = amountItemText.text.toString().trim().isNotEmpty())

            // Add a TextWatcher to monitor changes
            amountItemText.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // No-op
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // No-op
                }

                override fun afterTextChanged(s: android.text.Editable?) {
                    val text = s?.toString()?.trim() ?: ""
                    // Check if the text is not empty and not zero
                    val isEnabled = text.isNotEmpty()
                    handleButtonUI(buttonEnabled = isEnabled)
                }
            })
        }
    }

    private fun amountOfItemsPacked(): Int {
        val pickedAmount =
            packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                ?.sumOf { s -> s.packedAmount }
                ?: packingSharedViewModel.selectedPackingItemToPack?.amountToPack
                ?: packingSharedViewModel.selectedPackingItemToPack?.packedAmount ?: 0

        return pickedAmount
    }

    private fun handleButtonUI(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.saveAmountButton.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
            isEnabled = buttonEnabled
        }

    }

    private fun observeShippingContainersEvent() {
        viewModel.shippingContainersFlow.onEach { state ->
            when (state) {
                ShippingContainerViewState.Empty -> progressBarManager.dismiss()
                is ShippingContainerViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is ShippingContainerViewState.GetShippingContainersResult -> {
                    progressBarManager.dismiss()
                }

                ShippingContainerViewState.Loading -> progressBarManager.show()
                ShippingContainerViewState.Reset -> progressBarManager.dismiss()
                is ShippingContainerViewState.GetShippingContainerTypesResult -> {
                    progressBarManager.dismiss()

                }

                is ShippingContainerViewState.GetShippingContainerPackingListItemsByShippingContainerResult -> {
                    progressBarManager.dismiss()
                    callUpdateShippingContainerApi(state.shippingContainerTypes)
                }

                is ShippingContainerViewState.CreateShippingContainerPackingListItemResult -> {
                    progressBarManager.dismiss()
                    if (state.shippingContainerPackingListItemCreated == true) {

                    }

                }

                is ShippingContainerViewState.UpdateShippingContainerPackingListItemResult -> {
                    progressBarManager.dismiss()
                    if (state.shippingContainerPackingListItemUpdated == true) {
                        selectedShippingContainer?.let {
                            val action =
                                AmountFragmentDirections.actionAmountFragment3ToShippingContainerDetailsFragment(
                                    it
                                )
                            findNavController().navigate(action)
                        }
                    }

                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun callUpdateShippingContainerApi(shippingContainerTypes: ShippingContainerPackingListItemsByShippingContainerResponseModel?) {
        val item =
            shippingContainerTypes?.items?.firstOrNull { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
        viewModel.onEvent(
            ShippingContainersEvent.UpdateShippingContainerPackingListItem(
                packingListId = packingListId,
                createShippingContainerPackingListItemRequestModel = CreateShippingContainerPackingListItemRequestModel(
                    code = shippingContainerTypes?.code,
                    depth = shippingContainerTypes?.depth,
                    description = item?.description,
                    grossWeight = shippingContainerTypes?.own_weight,
                    height = shippingContainerTypes?.height,
                    items = mapToItemList(
                        listOf(
                            packingSharedViewModel.selectedPackingItemToPack!!
                        ) ?: emptyList()

                    ),
                    netWeight = shippingContainerTypes?.netWeight,
                    packingListId = packingListId,
                    reference = shippingContainerTypes?.reference,
                    shippingContainerId = shippingContainerTypes?.shippingContainerId,
                    sscc = shippingContainerTypes?.sscc,
                    width = shippingContainerTypes?.width
                )
            )
        )
    }

    private fun mapToItemList(packingListItems: List<PackingItem>): List<com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.Item> {
        return packingListItems.map { packingListItem ->
            com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.Item(
                description = packingListItem.description,
                iposition = packingListItem.iposition,
                lgid = packingListItem.lgid,
                orderItemPosition = packingListItem.orderItemPosition,
                packedAmount = binding.amountItemText.text.toString(),
                packedAmountItem = binding.amountItemText.text.toString(),
                packingListId = packingListItem.packingListId,
                position = packingListItem.position,
                typeCode = packingListItem.typeCode,
                unitCode = packingListItem.unitCode

            )
        }
    }


    private fun isDataValid(): Boolean {

        var amountToPack = binding.amountItemText.text.toString().trim().filter { it.isDigit() }
        if (amountToPack.isEmpty())
            amountToPack = "0"
        return amountToPack.toInt() <= amountOfItemsPacked()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(ShippingContainersEvent.Empty)
    }

}