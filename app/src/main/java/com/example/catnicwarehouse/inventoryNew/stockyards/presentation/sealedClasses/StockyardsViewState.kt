package com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel

sealed class StockyardsViewState{
    object Reset : StockyardsViewState()
    object Empty : StockyardsViewState()
    object Loading : StockyardsViewState()
    data class Error(val errorMessage: String?) : StockyardsViewState()
    data class GetWarehouseStockyardsByWarehouseCodeResult(val stockyards: ArrayList<WarehouseStockyardsDTO>?) : StockyardsViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?,val fromUserInteraction:Boolean) : StockyardsViewState()
    data class GetCurrentInventoriesResult(val currentInventory: CurrentInventoryResponseModel?) : StockyardsViewState()
}