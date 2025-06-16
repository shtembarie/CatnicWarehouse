package com.example.catnicwarehouse.dashboard.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.dashboard.domain.useCase.CreateMovementUseCase
import com.example.catnicwarehouse.dashboard.domain.useCase.GetMovementsUseCase
import com.example.catnicwarehouse.dashboard.domain.useCase.GetRightsUseCase
import com.example.catnicwarehouse.dashboard.domain.useCase.GetWarehousesUseCase
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardEvent
import com.example.catnicwarehouse.dashboard.presentation.sealedClasses.DashboardViewState
import com.example.catnicwarehouse.incoming.amountItem.domain.useCases.GetArticleUnitsUseCase
import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemEvent
import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemViewState
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.movements.CreateMovementRequest
import com.example.shared.tools.data.Resource

import com.example.catnicwarehouse.incoming.inventoryItems.data.domain.usecase.FindInventoryUseCase
import com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses.InventoryEvent
import com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses.InventoryViewState
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.catnicwarehouse.inventoryNew.shared.activity.InventoryActivity
import com.example.catnicwarehouse.inventoryNew.stockyards.domain.useCase.GetCurrentInventoryUseCase
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.movement.matchFound.domain.useCases.GetWarehouseStockyardInventoryEntriesUseCase
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.shared.repository.inventory.model.InventoryResponse
import com.example.shared.repository.inventory.model.InventoryUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val getMovementsUseCase: GetMovementsUseCase,
    private val createMovementUseCase: CreateMovementUseCase,
    private val warehousesUseCase: GetWarehousesUseCase,
    private val findInventoryUseCase: FindInventoryUseCase,
    private val getWarehouseStockyardInventoryEntriesUseCase: GetWarehouseStockyardInventoryEntriesUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
    private val getCurrentInventoryUseCase: GetCurrentInventoryUseCase,
    private val getRightsUseCase: GetRightsUseCase
) : BaseViewModel() {


    private val _dashboardFlow = MutableStateFlow<DashboardViewState>(DashboardViewState.Empty)
    val dashboardFlow: StateFlow<DashboardViewState> = _dashboardFlow

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.GetMovements -> getMovements(event.status, event.onlyMyMovements)
            is DashboardEvent.CreateMovement -> createMovement(event.createMovementRequest)
            DashboardEvent.Reset -> _dashboardFlow.value = DashboardViewState.Empty
            DashboardEvent.GetWarehouses -> getWarehouses()
            is DashboardEvent.LoadInventory -> getCurrentInventory(
                warehouseCode = event.warehouseCode,
            )

            is DashboardEvent.GetWarehouseStockyardInventoryEntries -> getWarehouseStockyardInventoryEntries(
                event.articleId,
                event.stockyardId,
                event.warehouseCode,
                event.isFromUserEntry
            )

            is DashboardEvent.SearchArticle -> searchArticlesForDelivery(searchTerm = event.searchTerm)
            is DashboardEvent.GetWarehouseStockyardById -> getWarehouseStockyardById(id = event.id)
            DashboardEvent.GetRights -> getRights()
        }
    }

    private fun getMovements(
        status: String?,
        onlyMyMovements: Boolean? = true
    ) {
        if (hasNetwork()) {
            getMovementsUseCase.invoke(
                status = status,
                onlyMyMovements = onlyMyMovements
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _dashboardFlow.value = DashboardViewState.Loading
                    }

                    is Resource.Error -> {
                        _dashboardFlow.value = DashboardViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _dashboardFlow.value =
                            DashboardViewState.GetMovementsResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouses(
    ) {
        if (hasNetwork()) {
            warehousesUseCase.invoke().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _dashboardFlow.value = DashboardViewState.Loading
                    }

                    is Resource.Error -> {
                        _dashboardFlow.value = DashboardViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _dashboardFlow.value =
                            DashboardViewState.GetWarehousesResult(result.data)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun createMovement(
        createMovementRequest: CreateMovementRequest?
    ) {
        if (hasNetwork()) {
            createMovementUseCase.invoke(
                createMovementRequest = createMovementRequest
            ).onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _dashboardFlow.value = DashboardViewState.Loading
                    }

                    is Resource.Error -> {
                        _dashboardFlow.value = DashboardViewState.Error(result.message)
                    }

                    is Resource.Success -> {
                        _dashboardFlow.value =
                            DashboardViewState.CreateMovementResult(result.data?.id)
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getCurrentInventory(
        warehouseCode: String?,
    ) {
        if (hasNetwork()) {
            getCurrentInventoryUseCase.getCurrentInventory(warehouseCode = warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _dashboardFlow.value = DashboardViewState.Loading
                        }

                        is Resource.Error -> {
                            _dashboardFlow.value = DashboardViewState.HasInventories(hasInventories = false)
                            _dashboardFlow.value = DashboardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            val currentInventory = result.data
                            if(currentInventory?.inventoryItems.isNullOrEmpty()){
                                _dashboardFlow.value = DashboardViewState.HasInventories(hasInventories = false)
                            }else{
                                _dashboardFlow.value = DashboardViewState.HasInventories(hasInventories = true)

                            }
                        }
                    }

                }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }

    }

    private fun mapInventoryDTOToUIModel(inventoryResponseModel: InventoryResponse): InventoryUIModel {
        return InventoryUIModel(
            id = inventoryResponseModel.id,
            warehouseCode = inventoryResponseModel.warehouseCode,
            status = inventoryResponseModel.status,
            createdTimestamp = inventoryResponseModel.createdTimestamp

        )
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
                            _dashboardFlow.value = DashboardViewState.Loading
                        }

                        is Resource.Error -> {
                            _dashboardFlow.value =
                                DashboardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _dashboardFlow.value =
                                DashboardViewState.WarehouseStockyardInventoryEntriesResponse(
                                    warehouseStockyardInventoryEntriesResponse = result.data,
                                    isFromUserEntry = isFromUserEntry
                                )
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
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
                            _dashboardFlow.value = DashboardViewState.Loading
                        }

                        is Resource.Error -> {
                            _dashboardFlow.value = DashboardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _dashboardFlow.value = DashboardViewState.ArticleResult(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getWarehouseStockyardById(
        id: String
    ) {
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _dashboardFlow.value = DashboardViewState.Loading
                        }

                        is Resource.Error -> {
                            _dashboardFlow.value = DashboardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _dashboardFlow.value =
                                DashboardViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun getRights() {
        if (hasNetwork()) {
            getRightsUseCase.invoke()
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _dashboardFlow.value = DashboardViewState.Loading
                        }

                        is Resource.Error -> {
                            _dashboardFlow.value = DashboardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _dashboardFlow.value =
                                DashboardViewState.RightsResult(rights = result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _dashboardFlow.value =
                DashboardViewState.Error(getString(R.string.no_internet_connection))
        }
    }
}