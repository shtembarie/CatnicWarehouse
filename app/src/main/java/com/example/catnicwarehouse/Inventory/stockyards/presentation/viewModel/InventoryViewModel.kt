package com.example.catnicwarehouse.Inventory.stockyards.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.Inventory.stockyards.domain.useCase.GetInventoryByIdUseCase
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdEvent
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.inventory.model.*
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryByIdUseCase: GetInventoryByIdUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
    ) : BaseViewModel() {
    private val _getInventoryById = MutableStateFlow<GetInventoryByIdViewState>(
        GetInventoryByIdViewState.Empty)
    val getInventoryById: StateFlow<GetInventoryByIdViewState> = _getInventoryById
    fun onEvent(event: GetInventoryByIdEvent){
        when(event){
            is GetInventoryByIdEvent.LoadCurrentInventory -> { getCurrentInventory(event.warehouseCode) }

            is GetInventoryByIdEvent.SearchArticle -> { searchArticlesForInventory(event.searchTerm) }
            is GetInventoryByIdEvent.GetWarehouseStockyardById -> {getWarehouseStockyardById(event.id)}
            GetInventoryByIdEvent.Reset -> reset()
        }
    }
    private fun reset() {
        _getInventoryById.value = GetInventoryByIdViewState.Empty
    }
    private fun searchArticlesForInventory(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryById.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryById.value = GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getInventoryById.value =
                                GetInventoryByIdViewState.ArticlesForInventoryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryById.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun getWarehouseStockyardById(
        id: String
    ){
        if (hasNetwork()) {
            getWarehouseStockyardByIdUseCase.invoke(id = id)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getInventoryById.value = GetInventoryByIdViewState.Loading
                        }

                        is Resource.Error -> {
                            _getInventoryById.value = GetInventoryByIdViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getInventoryById.value = GetInventoryByIdViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryById.value =
                GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun getCurrentInventory(warehouseCode: String) {
        if (hasNetwork()) {
            getInventoryByIdUseCase.getCurrentInventory(warehouseCode)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            showProgressBar()
                            _getInventoryById.value = GetInventoryByIdViewState.Loading
                        }
                        is Resource.Error -> {
                            _getInventoryById.value = GetInventoryByIdViewState.Error(result.message)
                        }
                        is Resource.Success -> {
                            val currentInventory = result.data
                            if (currentInventory != null) {
                                val uiModel = mapInventoryDTOToUIModelCurrent(currentInventory)
                                _getInventoryById.value = GetInventoryByIdViewState.GetCurrentInventory(uiModel)
                            } else {
                                _getInventoryById.value = GetInventoryByIdViewState.Error("No inventory found.")
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getInventoryById.value = GetInventoryByIdViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun mapInventoryDTOToUIModelCurrent(getInventoryByIdResponseModel: CurrentInventoryResponseModel) : GetInventoryByIdUIModelCurrentInventory {
        return GetInventoryByIdUIModelCurrentInventory(
            id = getInventoryByIdResponseModel.id,
            warehouseCode = getInventoryByIdResponseModel.warehouseCode,
            createdBy = getInventoryByIdResponseModel.createdBy,
            changedBy = getInventoryByIdResponseModel.changedBy,
            createdTimestamp = getInventoryByIdResponseModel.createdTimestamp,
            changedTimestamp = getInventoryByIdResponseModel.changedTimestamp,
            startTime = getInventoryByIdResponseModel.startTime,
            status = getInventoryByIdResponseModel.status,
            endTime = getInventoryByIdResponseModel.endTime,
            inventoryItems = getInventoryByIdResponseModel.inventoryItems,
            warehouseStockYards = getInventoryByIdResponseModel.warehouseStockYards,
            warehouseName = getInventoryByIdResponseModel.warehouseName,
            necessaryBookings = getInventoryByIdResponseModel.necessaryBookings
        )
    }
}