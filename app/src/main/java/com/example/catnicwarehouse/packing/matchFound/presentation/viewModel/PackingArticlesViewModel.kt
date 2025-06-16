package com.example.catnicwarehouse.packing.matchFound.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.packing.addPackingItems.domain.useCase.GetItemsForPackingUseCase
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsViewState
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.CancelPackingListUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.GetDefaultPackingZonesUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases.PausePackingUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.matchFound.domain.useCases.ChangePackingAmountUseCase
import com.example.catnicwarehouse.packing.matchFound.domain.useCases.GetPackingListStatusUseCase
import com.example.catnicwarehouse.packing.matchFound.domain.useCases.PickAmountUseCase
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesViewState
import com.example.catnicwarehouse.packing.packingItem.domain.useCases.GetPackingItemsUseCase
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PackingArticlesViewModel @Inject constructor(
    private val pickAmountUseCase: PickAmountUseCase,
    private val changePackingAmountUseCase: ChangePackingAmountUseCase,
    private val getItemsForPackingUseCase: GetItemsForPackingUseCase,
    private val getPackingListStatusUseCase: GetPackingListStatusUseCase,
    private val defaultPackingZonesUseCase: GetDefaultPackingZonesUseCase,
    private val cancelPackingListUseCase: CancelPackingListUseCase,
    private val getPackingItemsUseCase: GetPackingItemsUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase,
) : BaseViewModel() {


    private val _packingArticlesFlow =
        MutableStateFlow<PackingArticlesViewState>(PackingArticlesViewState.Empty)
    val packingArticlesFlow: StateFlow<PackingArticlesViewState> = _packingArticlesFlow

    fun onEvent(event: PackingArticlesEvent) {
        when (event) {
            is PackingArticlesEvent.PickAmount -> pickAmount(
                event.packingListId,
                event.itemId,
                event.pickAmountRequestModel
            )

            PackingArticlesEvent.Reset -> _packingArticlesFlow.value =
                PackingArticlesViewState.Reset

            is PackingArticlesEvent.changePackedAmount -> changePackedAmount(
                event.packingListId,
                event.itemId,
                event.packedAmount
            )

            is PackingArticlesEvent.GetItemsForPacking -> getItemsForPacking(event.packingListId)
            is PackingArticlesEvent.GetPackingListStatus -> getPackingListStatus(event.packingListId)
            is PackingArticlesEvent.CancelPackingList -> cancelPackingList(
                event.id,
                event.cancelPackingRequestModel
            )

            PackingArticlesEvent.GetDefaultPackingZones -> getDefaultPackingZones()
            is PackingArticlesEvent.GetPackingItems -> getPackingItems(event.id)
            is PackingArticlesEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(
                articleId = event.articleId,
                stockYardId = event.stockyardId,
                warehouseCode = event.warehouseCode,
                isFromUserEntry = event.isFromUserEntry
            )
        }
    }

    private fun pickAmount(
        packingListId: String?,
        itemId: String?,
        pickAmountRequestModel: PickAmountRequestModel?
    ) {
        if (hasNetwork()) {
            pickAmountUseCase.invoke(packingListId, itemId, pickAmountRequestModel)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.PickAmountResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }


    private fun changePackedAmount(
        packingListId: String?,
        itemId: String?,
        amountPacked: Int?
    ) {
        if (hasNetwork()) {
            changePackingAmountUseCase.invoke(packingListId, itemId, amountPacked)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.ChangePackedAmountResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getItemsForPacking(
        packingListId: String,
    ) {
        if (hasNetwork()) {
            getItemsForPackingUseCase.invoke(packingListId)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.GetItemsForPackingResponse(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getPackingListStatus(
        packingListId: String,
    ) {
        if (hasNetwork()) {
            getPackingListStatusUseCase.invoke(packingListId)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.GetPackingListStatusResponse(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getDefaultPackingZones() {
        if (hasNetwork()) {
            defaultPackingZonesUseCase.invoke(IncomingConstants.WarehouseParam)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.GetDefaultPackingZonesResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.GetCancelPackingListResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                        _packingArticlesFlow.value = PackingArticlesViewState.Loading
                    }

                    is Resource.Error -> {
                        _packingArticlesFlow.value = PackingArticlesViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _packingArticlesFlow.value =
                            PackingArticlesViewState.GetPackingItemsResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
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
                            _packingArticlesFlow.value = PackingArticlesViewState.Loading
                        }

                        is Resource.Error -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _packingArticlesFlow.value =
                                PackingArticlesViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _packingArticlesFlow.value =
                PackingArticlesViewState.Error(getString(R.string.no_internet_connection))
        }
    }


}