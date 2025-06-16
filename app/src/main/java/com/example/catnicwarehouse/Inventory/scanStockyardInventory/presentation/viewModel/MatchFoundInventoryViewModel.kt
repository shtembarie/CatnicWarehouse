package com.example.catnicwarehouse.Inventory.scanStockyardInventory.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.matchFound.domain.useCase.BookDeliveryItemUseCase
import com.example.catnicwarehouse.incoming.matchFound.domain.useCase.CreateDeliveryItemUseCase
import com.example.catnicwarehouse.incoming.matchFound.domain.useCase.UpdateDeliveryItemUseCase
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundEvent
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MatchFoundInventoryViewModel @Inject constructor(
    private val createDeliveryItemUseCase: CreateDeliveryItemUseCase,
    private val bookDeliveryItemUseCase: BookDeliveryItemUseCase,
    private val updateDeliveryItemUseCase: UpdateDeliveryItemUseCase
) : BaseViewModel() {


    private val _matchFoundFlow =
        MutableStateFlow<MatchFoundViewState>(MatchFoundViewState.Empty)
    val matchFoundFlow: StateFlow<MatchFoundViewState> = _matchFoundFlow


    private fun createInventoryItem(
        deliveryId: String,
        createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ) {
        if (hasNetwork()) {
            createDeliveryItemUseCase.invoke(
                deliveryId = deliveryId,
                createDeliveryItemRequestModel = createDeliveryItemRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _matchFoundFlow.value = MatchFoundViewState.Loading
                    }

                    is Resource.Error -> {
                        _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _matchFoundFlow.value =
                            MatchFoundViewState.DeliveryItemCreated(
                                deliveryId = deliveryId,
                                deliveryItemId = result.data ?: 0
                            )
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun bookDeliveryItem(
        deliveryId: String,
        deliveryItemId: String
    ) {
        if (hasNetwork()) {
            bookDeliveryItemUseCase.invoke(
                deliveryId = deliveryId,
                deliveryItemId = deliveryItemId
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _matchFoundFlow.value = MatchFoundViewState.Loading
                    }

                    is Resource.Error -> {
                        _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _matchFoundFlow.value =
                            MatchFoundViewState.DeliveryItemBooked(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun updateDeliveryItem(
        deliveryId: String,
        deliveryItemId: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ) {
        if (hasNetwork()) {
            updateDeliveryItemUseCase.invoke(
                deliveryId = deliveryId,
                deliveryItemId = deliveryItemId,
                updateDeliveryItemRequestModel = updateDeliveryItemRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _matchFoundFlow.value = MatchFoundViewState.Loading
                    }

                    is Resource.Error -> {
                        _matchFoundFlow.value = MatchFoundViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _matchFoundFlow.value =
                            MatchFoundViewState.DeliveryItemUpdated(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _matchFoundFlow.value =
                MatchFoundViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    fun onEvent(event: MatchFoundEvent) {
        when (event) {
            is MatchFoundEvent.CreateDeliveryItem -> createInventoryItem(
                event.deliveryId,
                event.createDeliveryItemRequestModel
            )

            is MatchFoundEvent.BookDeliveryItem -> bookDeliveryItem(
                event.deliveryId,
                event.deliveryItemId
            )

            MatchFoundEvent.Reset -> _matchFoundFlow.value = MatchFoundViewState.Reset
            is MatchFoundEvent.UpdateDeliveryItem -> updateDeliveryItem(
                event.deliveryId,
                event.deliveryItemId,
                event.updateDeliveryItemRequestModel
            )
        }
    }
}
