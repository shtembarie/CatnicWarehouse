package com.example.catnicwarehouse.incoming.deliveries.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.deliveries.domain.model.DeliveryUIModel
import com.example.catnicwarehouse.incoming.deliveries.domain.usecase.FindDeliveriesUseCase
import com.example.catnicwarehouse.incoming.deliveries.domain.usecase.GetDeliveryUseCase
import com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses.DeliveryEvent
import com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses.DeliveryViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val findDeliveriesUseCase: FindDeliveriesUseCase,
    private val getDeliveryUseCase: GetDeliveryUseCase
) : BaseViewModel() {


    private val _deliveryFlow = MutableStateFlow<DeliveryViewState>(DeliveryViewState.Empty)
    val deliveryFlow: StateFlow<DeliveryViewState> = _deliveryFlow

    fun onEvent(event: DeliveryEvent) {
        when (event) {
            DeliveryEvent.LoadDelivery -> findDelivery()
            is DeliveryEvent.GetDelivery -> getDeliveryById(event.id)
            DeliveryEvent.Reset -> reset()
        }
    }

    private fun reset() {
        _deliveryFlow.value = DeliveryViewState.Empty
    }

    private fun findDelivery(
        date: String? = null,
        user: String? = null,
        status: String? = null
    ) {

        if (hasNetwork()) {
            findDeliveriesUseCase.invoke(date = date, user = user, status = status)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _deliveryFlow.value = DeliveryViewState.Loading
                        }

                        is Resource.Error -> {
                            _deliveryFlow.value = DeliveryViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _deliveryFlow.value =
                                DeliveryViewState.Deliveries(result.data?.map {
                                    mapDeliveryDTOToUIModel(
                                        it
                                    )
                                })
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _deliveryFlow.value =
                DeliveryViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getDeliveryById(
        id: String
    ) {

        if (hasNetwork()) {
            getDeliveryUseCase.invoke(id=id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _deliveryFlow.value = DeliveryViewState.Loading
                        }

                        is Resource.Error -> {
                            _deliveryFlow.value = DeliveryViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _deliveryFlow.value = DeliveryViewState.Delivery(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _deliveryFlow.value =
                DeliveryViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun mapDeliveryDTOToUIModel(deliveryResponseModel: DeliveryResponseModel): DeliveryUIModel {
        return DeliveryUIModel(
            title = deliveryResponseModel.id,
            vendorId = deliveryResponseModel.vendorId ,
            supplier = deliveryResponseModel.createdBy ?: "",
            date = deliveryResponseModel.changedTimeStamp ?: "",
            state = deliveryResponseModel.status ?: "",
            customerId = deliveryResponseModel.customerId,
            type = deliveryResponseModel.type,
            customerAddressCompany1 = deliveryResponseModel.customerAddressCompany1 ?: "",
            warehouseCode = deliveryResponseModel.warehouseCode
        )
    }
}