package com.example.catnicwarehouse.incoming.deliveries.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDeliveryBinding
import com.example.catnicwarehouse.incoming.deliveries.domain.model.DeliveryUIModel
import com.example.catnicwarehouse.incoming.deliveries.presentation.adapter.DeliveryAdapter
import com.example.catnicwarehouse.incoming.deliveries.presentation.adapter.DeliveryAdapterListInteraction
import com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses.DeliveryEvent
import com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses.DeliveryViewState
import com.example.catnicwarehouse.incoming.deliveries.presentation.viewModel.DeliveryViewModel
import com.example.catnicwarehouse.incoming.deliveryType.presentation.bottomSheet.DeliveryTypeBottomSheet
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryStatus
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.google.android.gms.common.api.internal.SignInConnectionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DeliveryFragment : BaseFragment(), DeliveryAdapterListInteraction {
    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private lateinit var deliveryAdapter: DeliveryAdapter
    private var deliveries: List<DeliveryUIModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.onEvents(
            SharedEvent.Reset
        )
        handelHeaderSection()
        setUpAdapter()
        observeDeliveryResponse()
        addNewDelivery()
        handleShowAllDeliveriesSwitch()
        viewModel.onEvent(DeliveryEvent.LoadDelivery)
    }

    private fun handleShowAllDeliveriesSwitch() {
        binding.showAllDeliveriesSwitch.visibility = View.VISIBLE


        binding.showAllDeliveriesSwitch.setOnCheckedChangeListener { compoundButton, isSwitchOn ->
            if (isSwitchOn)
                deliveryAdapter.submitList(deliveries?.filter { s -> s.warehouseCode == IncomingConstants.WarehouseParam})
            else
                deliveryAdapter.submitList(deliveries?.filter { s -> s.state != "END"  && s.warehouseCode == IncomingConstants.WarehouseParam})
        }
    }

    private fun addNewDelivery() {
        binding.newDeliveryView.setOnClickListener {
            val bottomSheet = DeliveryTypeBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun observeDeliveryResponse() {
        viewModel.deliveryFlow.onEach { state ->
            when (state) {
                DeliveryViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is DeliveryViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                DeliveryViewState.Loading -> {
                    progressBarManager.show()
                }

                DeliveryViewState.Reset -> {
                    progressBarManager.dismiss()
                }

                is DeliveryViewState.Deliveries -> {
                    progressBarManager.dismiss()
                    deliveries = state.deliveries?.filter { s -> s.warehouseCode == IncomingConstants.WarehouseParam}
                    if (binding.showAllDeliveriesSwitch.isChecked)
                        deliveryAdapter.submitList(deliveries)
                    else
                        deliveryAdapter.submitList(deliveries?.filter { s -> s.state != "END" && s.state != "BKD" })
                }

                is DeliveryViewState.Delivery -> {
                    progressBarManager.dismiss()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setUpAdapter() {
        deliveryAdapter = DeliveryAdapter(interaction = this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.deliveryList.layoutManager = layoutManager
        binding.deliveryList.adapter = deliveryAdapter
    }


    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.deliveries)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onViewClicked(data: DeliveryUIModel) {

        if (data.state == DeliveryStatus.OPN.name) {
            sharedViewModel.onEvents(
                SharedEvent.Reset,
                SharedEvent.UpdateSupplierInfo(
                    VendorOrCustomerInfo(
                        vendorId = data.vendorId,
                        name = data.supplier,
                        customerId = data.customerId
                    )
                ),
                SharedEvent.UpdateDeliveryId(data.title)
            )

            findNavController().navigate(
                R.id.articlesFragment
            )
        } else {
            showErrorBanner(getString(R.string.the_delivery_has_been_completed))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}