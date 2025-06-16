package com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.useCase.GetWarehouseStockYardsUseCase
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses.GetWarehouseStockYardEvent
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses.GetWarehouseStockYardViewState
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdEvent
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.scan.domain.usecase.GetWarehouseStockyardByIdUseCase
import com.example.catnicwarehouse.scan.domain.usecase.SearchArticlesForDeliveryUseCase
import com.example.catnicwarehouse.tools.ext.getString
import com.example.catnicwarehouse.tools.ext.hasNetwork
import com.example.shared.repository.correctingStock.model.GetCorrectionByIdUIModelCurrentInventory
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsList
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Enoklit on 07.11.2024.
 */
@HiltViewModel
class WarehouseStockYardViewModel @Inject constructor(
    private val getWarehouseStockYardsUseCase: GetWarehouseStockYardsUseCase,
    private val searchArticlesForDeliveryUseCase: SearchArticlesForDeliveryUseCase,
    private val getWarehouseStockyardByIdUseCase: GetWarehouseStockyardByIdUseCase,
): BaseViewModel() {
    private val _getWarehouseStockYard = MutableStateFlow<GetWarehouseStockYardViewState>(
        GetWarehouseStockYardViewState.Empty)
    val getWarehouseStockYard: StateFlow<GetWarehouseStockYardViewState> = _getWarehouseStockYard

    fun onEvent(event : GetWarehouseStockYardEvent){
        when(event){
            is GetWarehouseStockYardEvent.Loading -> { getWarehouseStockYardList(event.warehouseCode)}
            is GetWarehouseStockYardEvent.Reset -> reset()
            is GetWarehouseStockYardEvent.SearchArticle -> {searchArticlesForInventory(event.searchTerm)}
            is GetWarehouseStockYardEvent.GetWarehouseStockyardById -> {getWarehouseStockyardById(event.id)}
        }
    }
    private fun reset() {
        _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Empty
    }
    private fun getWarehouseStockYardList(warehouseCode: String?){
        if (hasNetwork()){
            getWarehouseStockYardsUseCase.getWarehouseStockyard(warehouseCode)
                .onEach { result ->
                    when (result){
                        is Resource.Loading -> {
                            showProgressBar()
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Loading
                        }
                        is Resource.Error -> {
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Error("Not found")
                        }
                        is Resource.Success -> {
                            val correctionStockyard = result.data
                            if (correctionStockyard != null){
                                val uiModel = mapCorrectingStockYardDTOToUIModelCurrent(correctionStockyard)
                                _getWarehouseStockYard.value = GetWarehouseStockYardViewState.WarehouseStockFound(uiModel)
                            }else {
                                _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Error("")
                            }
                        }
                        }
                    }.launchIn(viewModelScope)
                } else {
                    _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Error(getString(R.string.no_internet_connection))
        }
    }
    private fun searchArticlesForInventory(
        searchTerm: String
    ) {
        if (hasNetwork()) {
            searchArticlesForDeliveryUseCase.invoke(searchTerm = searchTerm)
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Loading
                        }

                        is Resource.Error -> {
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getWarehouseStockYard.value =
                                GetWarehouseStockYardViewState.ArticlesForInventoryFound(result.data)
                        }

                    }
                }.launchIn(viewModelScope)
        } else {
            _getWarehouseStockYard.value =
                GetWarehouseStockYardViewState.Error(getString(R.string.no_internet_connection))
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
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Loading
                        }

                        is Resource.Error -> {
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.Error(result.message)
                        }

                        is Resource.Success -> {
                            _getWarehouseStockYard.value = GetWarehouseStockYardViewState.WarehouseStockByIdFound(warehouseStockyard = result.data)

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _getWarehouseStockYard.value =
                GetWarehouseStockYardViewState.Error(getString(R.string.no_internet_connection))
        }
    }

    private fun mapCorrectingStockYardDTOToUIModelCurrent(correctionStockyards: List<WarehouseStockYardsList>): List<GetCorrectionByIdUIModelCurrentInventory> {
        return correctionStockyards.map { stockyard ->
            GetCorrectionByIdUIModelCurrentInventory(
                id = stockyard.id,
                hierarchyLevel = stockyard.hierarchyLevel,
                name = stockyard.name,
                warehouseTemplateId = stockyard.warehouseTemplateId,
                warehouseCode = stockyard.warehouseCode,
                parentStockId = stockyard.parentStockId,
                length = stockyard.length,
                width = stockyard.width,
                height = stockyard.height,
                geoLocationX = stockyard.geoLocationX,
                geoLocationY = stockyard.geoLocationY,
                currentWeight = stockyard.currentWeight,
                defaultPickAndDropZone = stockyard.defaultPickAndDropZone,
                defaultPackingZone = stockyard.defaultPackingZone
            )
        }
    }

}
















