package com.example.catnicwarehouse.incoming.deliveryDetail.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.deliveries.domain.usecase.GetDeliveryUseCase
import com.example.catnicwarehouse.incoming.deliveryDetail.domain.useCase.CompleteDeliveryWarehousingUseCase
import com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass.DeliveryDetailsEvent
import com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass.DeliveryDetailsViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DeliveryDetailsViewModel @Inject constructor(
    private val getDeliveryUseCase: GetDeliveryUseCase,
    private val completeDeliveryWarehousingUseCase: CompleteDeliveryWarehousingUseCase
) : BaseViewModel() {


    private val _deliveryDetailsFlow = MutableStateFlow<DeliveryDetailsViewState>(DeliveryDetailsViewState.Empty)
    val deliveryDetailsFlow: StateFlow<DeliveryDetailsViewState> = _deliveryDetailsFlow

    fun onEvent(event: DeliveryDetailsEvent) {
        when (event) {
            is DeliveryDetailsEvent.GetDelivery -> getDeliveryById(event.id)
            DeliveryDetailsEvent.Reset -> reset()
            is DeliveryDetailsEvent.CompleteDelivery -> completeDeliveryWarehousing(id = event.id)
        }
    }

    private fun reset() {
        _deliveryDetailsFlow.value = DeliveryDetailsViewState.Empty
    }


    private fun getDeliveryById(
        id: String
    ) {

        if (hasNetwork()) {
            getDeliveryUseCase.invoke(id=id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.Loading
                        }

                        is Resource.Error -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.Delivery(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _deliveryDetailsFlow.value =
                DeliveryDetailsViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun completeDeliveryWarehousing(
        id: String
    ) {

        if (hasNetwork()) {
            completeDeliveryWarehousingUseCase.invoke(deliveryId =id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.Loading
                        }

                        is Resource.Error -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _deliveryDetailsFlow.value = DeliveryDetailsViewState.DeliveryCompleted(result.data)
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _deliveryDetailsFlow.value =
                DeliveryDetailsViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}