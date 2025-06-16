package com.example.catnicwarehouse.movement.stockyards.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

sealed class StockyardsViewState{
    object Reset : StockyardsViewState()
    object Empty : StockyardsViewState()
    object Loading : StockyardsViewState()
    data class Error(val errorMessage: String?) : StockyardsViewState()
    data class GetWarehouseStockyardsByWarehouseCodeResult(val stockyards: ArrayList<WarehouseStockyardsDTO>?) : StockyardsViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : StockyardsViewState()
}