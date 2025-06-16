package com.example.catnicwarehouse.packing.shippingContainer.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentShippingContainerBinding
import com.example.catnicwarehouse.databinding.FragmentShippingContainerDetailsBinding
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainerItemsListAdapter
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainersItemsListAdapterInteraction
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainersListAdapter
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainerViewState
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainersEvent
import com.example.catnicwarehouse.packing.shippingContainer.presentation.viewModel.ShippingContainersViewModel
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.Item
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.Data
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ShippingContainerDetailsFragment : BaseFragment(),ShippingContainersItemsListAdapterInteraction {

    private var _binding: FragmentShippingContainerDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShippingContainersViewModel by viewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()

    private var packingListId: String? = null
    private var selectedShippingContainer: Data? = null

    private lateinit var shippingContainerItemsListAdapter: ShippingContainerItemsListAdapter

    private val args: ShippingContainerDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShippingContainerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        packingListId = packingSharedViewModel.selectedPackingListItem?.id
        selectedShippingContainer = args.selectedShippingContainer
        setUpAdapter()
        observeShippingContainerItemListEvent()

        binding.closeShippingContainerButton.setOnClickListener {
            findNavController().popBackStack(R.id.shippingContainerFragment,false)
        }

        viewModel.onEvent(
            ShippingContainersEvent.GetShippingContainerPackingListItemsByShippingContainer(
                packingListId = packingListId,
                shippingContainerId = selectedShippingContainer?.Id
            )
        )

    }


    private fun setUpAdapter() {
        shippingContainerItemsListAdapter =
            ShippingContainerItemsListAdapter(interaction = this, context = requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = shippingContainerItemsListAdapter
    }


    private fun observeShippingContainerItemListEvent() {
        viewModel.shippingContainersFlow.onEach { state ->
            when (state) {

                is ShippingContainerViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                ShippingContainerViewState.Loading -> progressBarManager.show()

                is ShippingContainerViewState.GetShippingContainerPackingListItemsByShippingContainerResult -> {
                    progressBarManager.dismiss()
                    if(state.shippingContainerTypes?.items.isNullOrEmpty()){
                        binding.emptyLayout.visibility =View.VISIBLE
                        binding.packingList.visibility = View.GONE
                    }else{
                        binding.emptyLayout.visibility =View.GONE
                        binding.packingList.visibility = View.VISIBLE
                    }
                    state.shippingContainerTypes?.items?.let {
                        shippingContainerItemsListAdapter.submitList(sortItemsByOrderPosition(items = it))
                    }
                    handleHeaderSection(state.shippingContainerTypes?.shippingContainerId?:selectedShippingContainer?.Id?:"")
                }

               else ->{
                   progressBarManager.dismiss()
               }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun sortItemsByOrderPosition(items: List<Item>): List<Item> {
        return items.sortedBy { it.orderItemPosition.toIntOrNull() ?: Int.MAX_VALUE }
    }


    override fun onViewClicked(data: Item) {

    }

    private fun handleHeaderSection(header:String) {
        binding.deliveryHeader.headerTitle.text = header
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack(R.id.shippingContainerFragment,false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(ShippingContainersEvent.Empty)
    }


}