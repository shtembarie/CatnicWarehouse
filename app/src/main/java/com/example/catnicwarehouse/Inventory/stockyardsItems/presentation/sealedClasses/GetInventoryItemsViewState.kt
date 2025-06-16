package com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.inventory.model.InventoryItem

sealed class GetInventoryItemsViewState {
    object Reset : GetInventoryItemsViewState()
    object Empty : GetInventoryItemsViewState()
    object Loading : GetInventoryItemsViewState()
    data class GetInventoriesItems(val items: List<InventoryItem>) : GetInventoryItemsViewState()
    data class Error(val errorMessage: String?) : GetInventoryItemsViewState()
    object InventoryArticleItemAdded : GetInventoryItemsViewState()
    data class ArticlesForInventoryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : GetInventoryItemsViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : GetInventoryItemsViewState()
}
