package com.example.catnicwarehouse.packing.shippingContainer.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.packing.shippingContainer.domain.useCase.CreateShippingContainerPackingListItemUseCase
import com.example.catnicwarehouse.packing.shippingContainer.domain.useCase.GetShippingContainerPackingListItemsByShippingContainerUseCase
import com.example.catnicwarehouse.packing.shippingContainer.domain.useCase.GetShippingContainerTypesUseCase
import com.example.catnicwarehouse.packing.shippingContainer.domain.useCase.GetShippingContainersUseCase
import com.example.catnicwarehouse.packing.shippingContainer.domain.useCase.UpdateShippingContainerPackingListItemUseCase
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainerViewState
import com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses.ShippingContainersEvent
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.Data
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ShippingContainersViewModel @Inject constructor(
    private val getShippingContainersUseCase: GetShippingContainersUseCase,
    private val getShippingContainerTypesUseCase: GetShippingContainerTypesUseCase,
    private val createShippingContainerPackingListItemUseCase: CreateShippingContainerPackingListItemUseCase,
    private val updateShippingContainerPackingListItemUseCase: UpdateShippingContainerPackingListItemUseCase,
    private val getShippingContainerPackingListItemsByShippingContainerUseCase: GetShippingContainerPackingListItemsByShippingContainerUseCase
) : BaseViewModel() {


    private val _shippingContainersFlow =
        MutableStateFlow<ShippingContainerViewState>(ShippingContainerViewState.Empty)
    val shippingContainersFlow: StateFlow<ShippingContainerViewState> = _shippingContainersFlow


    fun onEvent(event: ShippingContainersEvent) {
        when (event) {
            ShippingContainersEvent.Empty -> _shippingContainersFlow.value =
                ShippingContainerViewState.Empty

            is ShippingContainersEvent.GetShippingContainers -> getShippingContainers(event.packingListId)
            ShippingContainersEvent.GetShippingContainerTypes -> getShippingContainerTypes()
            is ShippingContainersEvent.CreateShippingContainerPackingListItem -> createShippingContainerPackingListItem(
                event.packingListId,
                event.createShippingContainerPackingListItemRequestModel
            )

            is ShippingContainersEvent.UpdateShippingContainerPackingListItem -> updateShippingContainerPackingListItem(
                event.packingListId,
                event.createShippingContainerPackingListItemRequestModel
            )

            is ShippingContainersEvent.GetShippingContainerPackingListItemsByShippingContainer -> getShippingContainerPackingListItemsByShippingContainer(
                event.packingListId,
                event.shippingContainerId
            )
        }
    }

    private fun getShippingContainers(
        packingListId: String?,
    ) {
        if (hasNetwork()) {
            getShippingContainersUseCase.invoke(
                id = packingListId,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _shippingContainersFlow.value = ShippingContainerViewState.Loading
                    }

                    is Resource.Error -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.GetShippingContainersResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _shippingContainersFlow.value =
                ShippingContainerViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getShippingContainerTypes(
    ) {
        if (hasNetwork()) {
            getShippingContainerTypesUseCase.invoke().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _shippingContainersFlow.value = ShippingContainerViewState.Loading
                    }

                    is Resource.Error -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.GetShippingContainerTypesResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _shippingContainersFlow.value =
                ShippingContainerViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun createShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ) {
        if (hasNetwork()) {
            createShippingContainerPackingListItemUseCase.invoke(
                packingListId,
                createShippingContainerPackingListItemRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _shippingContainersFlow.value = ShippingContainerViewState.Loading
                    }

                    is Resource.Error -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.CreateShippingContainerPackingListItemResult(
                                result.data
                            )
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _shippingContainersFlow.value =
                ShippingContainerViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun updateShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ) {
        if (hasNetwork()) {
            updateShippingContainerPackingListItemUseCase.invoke(
                packingListId,
                createShippingContainerPackingListItemRequestModel
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _shippingContainersFlow.value = ShippingContainerViewState.Loading
                    }

                    is Resource.Error -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.UpdateShippingContainerPackingListItemResult(
                                result.data
                            )
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _shippingContainersFlow.value =
                ShippingContainerViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun getShippingContainerPackingListItemsByShippingContainer(
        packingListId: String?,
        shippingContainerId: String?
    ) {
        if (hasNetwork()) {
            getShippingContainerPackingListItemsByShippingContainerUseCase.invoke(
                id = packingListId,
                shippingContainerId = shippingContainerId
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _shippingContainersFlow.value = ShippingContainerViewState.Loading
                    }

                    is Resource.Error -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _shippingContainersFlow.value =
                            ShippingContainerViewState.GetShippingContainerPackingListItemsByShippingContainerResult(
                                result.data
                            )
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _shippingContainersFlow.value =
                ShippingContainerViewState.Error(getString(R.string.no_internet_connection))
        }
    }

}







