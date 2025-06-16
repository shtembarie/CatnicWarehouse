package com.example.catnicwarehouse.packing.finalisePackingList.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.CancelPackingListUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.FinalizePackingUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.GetDefaultPackingZonesUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.PausePackingUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingItemsUseCase
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingListCommentUseCase
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FinalisePackingViewModel @Inject constructor(
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase,
    private val pausePackingUseCase: PausePackingUseCase,
    private val getPackingItemsUseCase: GetPackingItemsUseCase,
    private val finalizePackingUseCase: FinalizePackingUseCase,
    private val cancelPackingListUseCase: CancelPackingListUseCase,
    private val defaultPackingZonesUseCase: GetDefaultPackingZonesUseCase,
    private val getPackingListCommentUseCase: GetPackingListCommentUseCase
) : BaseViewModel() {


    private val _finalisePackingFlow =
        MutableStateFlow<FinalisePackingViewState>(FinalisePackingViewState.Empty)
    val finalisePackingFlow: StateFlow<FinalisePackingViewState> = _finalisePackingFlow

    private val warehouseCode = IncomingConstants.WarehouseParam

    fun onEvent(event: FinalisePackingEvent) {
        when (event) {
            is FinalisePackingEvent.SearchArticle -> searchArticlesForDelivery(searchTerm = event.searchTerm)
            is FinalisePackingEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(
                articleId = event.articleId,
                stockYardId = event.stockyardId,
                warehouseCode = event.warehouseCode,
                isFromUserEntry = event.isFromUserEntry
            )

            is FinalisePackingEvent.PausePacking -> pausePacking(event.id)
            is FinalisePackingEvent.GetPackingItems -> getPackingItems(event.id)
            is FinalisePackingEvent.FinalizePackingList -> finalizePackingList(event.id)
            is FinalisePackingEvent.CancelPackingList -> cancelPackingList(
                event.id,
                event.cancelPackingRequestModel
            )

            is FinalisePackingEvent.GetDefaultPackingZones -> getDefaultPackingZones()
            is FinalisePackingEvent.Empty -> _finalisePackingFlow.value = FinalisePackingViewState.Empty
            is FinalisePackingEvent.GetPackingListComment -> getPackingListComment(event.id)
        }
    }

    private fun searchArticlesForDelivery(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _finalisePackingFlow.value = FinalisePackingViewState.Loading
                        }

                        is Resource.Error -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.ArticleResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockYardId: String?,
        warehouseCode: String?,
        isFromUserEntry: Boolean
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardInventoryEntriesUseCase.invoke(
                id = articleId,
                stockyardId = stockYardId,
                warehouseCode = warehouseCode
            )
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _finalisePackingFlow.value = FinalisePackingViewState.Loading
                        }

                        is Resource.Error -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun pausePacking(
        id: String?,
    ) {
        if (hasNetwork()) {
            pausePackingUseCase.invoke(
                id = id
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Loading
                    }

                    is Resource.Error -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _finalisePackingFlow.value =
                            FinalisePackingViewState.PausePackingResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getPackingItems(
        id: String?,
    ) {
        if (hasNetwork()) {
            getPackingItemsUseCase.invoke(
                id = id,
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Loading
                    }

                    is Resource.Error -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _finalisePackingFlow.value =
                            FinalisePackingViewState.GetPackingItemsResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun finalizePackingList(
        id: String?
    ) {
        if (hasNetwork()) {
            finalizePackingUseCase.invoke(id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _finalisePackingFlow.value = FinalisePackingViewState.Loading
                        }

                        is Resource.Error -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.GetFinalizePackingListResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun cancelPackingList(
        id: String?,
        cancelPackingRequestModel: CancelPackingRequestModel
    ) {
        if (hasNetwork()) {
            cancelPackingListUseCase.invoke(id, cancelPackingRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _finalisePackingFlow.value = FinalisePackingViewState.Loading
                        }

                        is Resource.Error -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.GetCancelPackingListResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun getDefaultPackingZones() {
        if (hasNetwork()) {
            defaultPackingZonesUseCase.invoke(IncomingConstants.WarehouseParam)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _finalisePackingFlow.value = FinalisePackingViewState.Loading
                        }

                        is Resource.Error -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _finalisePackingFlow.value =
                                FinalisePackingViewState.GetDefaultPackingZonesResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getPackingListComment(
        id: String?,
    ) {
        if (hasNetwork()) {
            getPackingListCommentUseCase.invoke(id).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Loading
                    }

                    is Resource.Error -> {
                        _finalisePackingFlow.value = FinalisePackingViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _finalisePackingFlow.value =
                            FinalisePackingViewState.GetPackingListComment(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _finalisePackingFlow.value =
                FinalisePackingViewState.Error(getString(R.string.no_internet_connection))
        }
    }


}