package com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

sealed class StockyardTreeViewState{
    object Reset : StockyardTreeViewState()
    object Empty : StockyardTreeViewState()
    object Loading : StockyardTreeViewState()
    data class WarehouseStockyardsFound(val warehouseStockyards: List<WarehouseStockyardsDTO>?,val isFromUserSearch:Boolean) : StockyardTreeViewState()
    data class Error(val errorMessage: String?) : StockyardTreeViewState()
}
