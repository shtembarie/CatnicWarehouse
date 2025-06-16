package com.example.catnicwarehouse.packing.shippingContainer.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentShippingContainerBinding
import com.example.catnicwarehouse.packing.shared.presentation.activity.PackingActivity
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainersListAdapter
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainersListAdapterInteraction
import com.example.catnicwarehouse.packing.shippingContainer.presentation.bottomSheet.ShippingContainerTypeBottomSheet
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainerViewState
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainersEvent
import com.example.catnicwarehouse.packing.shippingContainer.presentation.viewModel.ShippingContainersViewModel
import com.example.shared.networking.network.packing.model.packingItem.PackingItem
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.Data
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ShippingContainerFragment : BaseFragment(), ShippingContainersListAdapterInteraction {

    private var _binding: FragmentShippingContainerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShippingContainersViewModel by viewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private lateinit var shippingContainersListAdapter: ShippingContainersListAdapter
    private var packingListId: String? = null
    private var selectedShippingContainer: Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShippingContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        packingListId = packingSharedViewModel.selectedPackingListItem?.id
        binding.confirmButton.isEnabled = false
        handleConfirmButton(false)
        handelHeaderSection()
        setUpAdapter()
        observeShippingContainersEvent()
        handleConfirmButtonAction()
        handleAddButtonAction()
        handleShippingContainerResultBack()
        viewModel.onEvent(ShippingContainersEvent.GetShippingContainers(packingListId))
    }


    private fun handleShippingContainerResultBack() {
        parentFragmentManager.setFragmentResultListener(
            "ShippingContainerTypeSelected",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedType =
                bundle.getParcelable<ShippingContainerTypeResponseModelItem>("selectedType")

            val width = bundle.getString("width")?.toFloatOrNull()
            val height = bundle.getString("height")?.toFloatOrNull()
            val depth = bundle.getString("depth")?.toFloatOrNull()
            val netWeight = bundle.getString("netWeight")?.toFloatOrNull()
            val totalWeight = bundle.getString("totalWeight")?.toFloatOrNull()


            selectedType?.let {
                // Handle the selected container type
                viewModel.onEvent(
                    ShippingContainersEvent.CreateShippingContainerPackingListItem(
                        packingListId = packingListId,
                        createShippingContainerPackingListItemRequestModel = CreateShippingContainerPackingListItemRequestModel(
                            code = it.code,
                            depth = depth ?: it.depth,
                            description = it.description,
                            grossWeight = totalWeight ?: it.own_weight,
                            height = height ?: it.height,
                            items = mapToItemList(
                                listOf( packingSharedViewModel.selectedPackingItemToPack!! )
                            ),
                            netWeight = netWeight ?: it.own_weight,
                            packingListId = packingListId,
                            reference = "",
                            width = width ?: it.width
                        )
                    )
                )
            }
        }
    }


    private fun handleAddButtonAction() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            viewModel.onEvent(ShippingContainersEvent.GetShippingContainerTypes)
        }
    }

    private fun handleConfirmButtonAction() {
        binding.confirmButton.setOnClickListener {
            viewModel.onEvent(
                ShippingContainersEvent.GetShippingContainerPackingListItemsByShippingContainer(
                    packingListId = packingListId,
                    shippingContainerId = selectedShippingContainer?.Id
                )
            )
        }
    }

    private fun mapToItemList(packingListItems: List<PackingItem>): List<com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.Item> {
        return packingListItems.map { packingListItem ->
            com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.Item(
                description = packingListItem.description,
                iposition = packingListItem.iposition,
                lgid = packingListItem.lgid,
                orderItemPosition = packingListItem.orderItemPosition,
                packedAmount = (packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                    ?.sumOf { s -> s.packedAmount }
                    ?: packingSharedViewModel.selectedPackingItemToPack?.amountToPack
                    ?: packingSharedViewModel.selectedPackingItemToPack?.packedAmount
                    ?: 0).toString(),
                packedAmountItem = (packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                    ?.sumOf { s -> s.packedAmount }
                    ?: packingSharedViewModel.selectedPackingItemToPack?.amountToPack
                    ?: packingSharedViewModel.selectedPackingItemToPack?.packedAmount
                    ?: 0).toString(),
                packingListId = packingListItem.packingListId,
                position = packingListItem.position,
                typeCode = packingListItem.typeCode,
                unitCode = packingListItem.unitCode

            )
        }
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
                    items = mapToItemList(packingSharedViewModel.packingItems ?: emptyList()),
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


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.shipping_container)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.setImageDrawable(requireContext().getDrawable(R.drawable.add_trailing_icon))
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setUpAdapter() {
        shippingContainersListAdapter =
            ShippingContainersListAdapter(interaction = this, context = requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = shippingContainersListAdapter
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
                    if (state.shippingContainers != null && state.shippingContainers.data.isEmpty()
                            .not()
                    ) {
                        binding.emptyLayout.visibility = View.GONE
                        binding.packingList.visibility = View.VISIBLE
                        shippingContainersListAdapter.submitList(state.shippingContainers.data)
                    } else {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.packingList.visibility = View.GONE
                    }

                }

                ShippingContainerViewState.Loading -> progressBarManager.show()
                ShippingContainerViewState.Reset -> progressBarManager.dismiss()
                is ShippingContainerViewState.GetShippingContainerTypesResult -> {
                    progressBarManager.dismiss()
                    state.shippingContainerTypes?.let {
                        ShippingContainerTypeBottomSheet.newInstance(it)
                            .show(parentFragmentManager, "ShippingContainerType")
                    }
                }

                is ShippingContainerViewState.GetShippingContainerPackingListItemsByShippingContainerResult -> {
                    progressBarManager.dismiss()
                    callUpdateShippingContainerApi(state.shippingContainerTypes)
                }

                is ShippingContainerViewState.CreateShippingContainerPackingListItemResult -> {
                    progressBarManager.dismiss()
                    if (state.shippingContainerPackingListItemCreated == true){
                        findNavController().popBackStack(R.id.matchFoundFragment2, false)
                    }

                }

                is ShippingContainerViewState.UpdateShippingContainerPackingListItemResult -> {
                    progressBarManager.dismiss()
                    if (state.shippingContainerPackingListItemUpdated == true)
                        findNavController().popBackStack(R.id.matchFoundFragment2, false)
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
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


    override fun onViewClicked(data: Data) {
        selectedShippingContainer = data
        navigateToAmountFragment(data)
    }

    private fun navigateToAmountFragment(data: Data) {
        val action =
            ShippingContainerFragmentDirections.actionShippingContainerFragmentToAmountFragment3(
                data
            )
        findNavController().navigate(action)
    }


}