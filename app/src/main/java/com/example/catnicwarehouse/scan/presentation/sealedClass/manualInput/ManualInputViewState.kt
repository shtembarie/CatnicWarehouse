package com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class ManualInputViewState{
    object Reset : ManualInputViewState()
    object Empty : ManualInputViewState()
    object Loading : ManualInputViewState()
    data class ArticlesForDeliveryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : ManualInputViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : ManualInputViewState()
    data class WarehouseStockyardsFound(val warehouseStockyards: List<WarehouseStockyardsDTO>?,val isFromUserSearch:Boolean) : ManualInputViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): ManualInputViewState()
    data class Error(val errorMessage: String?) : ManualInputViewState()
}
