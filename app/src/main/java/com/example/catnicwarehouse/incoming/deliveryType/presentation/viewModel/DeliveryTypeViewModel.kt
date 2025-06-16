package com.example.catnicwarehouse.incoming.deliveryType.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.deliveryType.domain.useCase.CreateDeliveryUseCase
import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeEvent
import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DeliveryTypeViewModel @Inject constructor(
    private val createDeliveryUseCase: CreateDeliveryUseCase
) : BaseViewModel() {


    private val _deliveryFlow = MutableStateFlow<DeliveryTypeViewState>(DeliveryTypeViewState.Empty)
    val deliveryFlow: StateFlow<DeliveryTypeViewState> = _deliveryFlow


    private fun reset() {
        _deliveryFlow.value = DeliveryTypeViewState.Empty
    }

    private fun createDelivery(
        createDeliveryRequestModel: CreateDeliveryRequestModel
    ) {

        if (hasNetwork()) {
            createDeliveryUseCase.invoke(createDeliveryRequestModel = createDeliveryRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _deliveryFlow.value = DeliveryTypeViewState.Loading
                        }

                        is Resource.Error -> {
                            _deliveryFlow.value = DeliveryTypeViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _deliveryFlow.value =
                                DeliveryTypeViewState.DeliveryCreated(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _deliveryFlow.value =
                DeliveryTypeViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    fun onEvent(event: DeliveryTypeEvent) {
        when (event) {
            is DeliveryTypeEvent.CreateDelivery -> {
                createDelivery(event.createDeliveryRequestModel)
            }

            DeliveryTypeEvent.Reset -> reset()
        }
    }

}