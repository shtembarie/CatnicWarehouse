package com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.warehouse.FindWarehouseDTO

sealed class UnloadingStockyardsViewState{
    object Reset : UnloadingStockyardsViewState()
    object Empty : UnloadingStockyardsViewState()
    object Loading : UnloadingStockyardsViewState()

    data class WarehousesFound(val warehouses: List<FindWarehouseDTO>?) : UnloadingStockyardsViewState()
    data class DefaultPickUpAndDropZonesFound(val stockyards: List<WarehouseStockyardsDTO>?) : UnloadingStockyardsViewState()
    data class ArticlesForDeliveryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : UnloadingStockyardsViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : UnloadingStockyardsViewState()
    data class Error(val errorMessage: String?) : UnloadingStockyardsViewState()
}
