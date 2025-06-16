package com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses

import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses.MatchFoundInventoryViewState
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses.GetInventoryItemsViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.inventory.model.GetInventoryByIdUIModelCurrentInventory
import com.example.shared.repository.inventory.model.InventoryItem

sealed class GetInventoryByIdViewState {
    object Reset : GetInventoryByIdViewState()
    object Empty : GetInventoryByIdViewState()
    object Loading : GetInventoryByIdViewState()

    data class GetCurrentInventory(val getCurrentInventory: GetInventoryByIdUIModelCurrentInventory) : GetInventoryByIdViewState()
    data class ArticlesForInventoryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : GetInventoryByIdViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : GetInventoryByIdViewState()
    data class Error(val errorMessage: String?) : GetInventoryByIdViewState()

    data class GetInventoriesItems(val items: List<InventoryItem>) : GetInventoryByIdViewState()
    object InventoryArticleItemAdded : GetInventoryByIdViewState()

    data class InventoryItemUpdated(val isItemUpdated: Boolean?) : GetInventoryByIdViewState()
}