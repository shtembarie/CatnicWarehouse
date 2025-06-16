package com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class MatchFoundViewState{
    object Reset : MatchFoundViewState()
    object Empty : MatchFoundViewState()
    object Loading : MatchFoundViewState()
    data class Error(val errorMessage: String?) : MatchFoundViewState()
    data class PickUpResult(val isSuccess: Boolean?) : MatchFoundViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : MatchFoundViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?): MatchFoundViewState()
}