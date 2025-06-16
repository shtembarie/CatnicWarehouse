package com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class InventoryItemsViewState {
    object Empty : InventoryItemsViewState()
    object Loading : InventoryItemsViewState()
    data class Error(val errorMessage: String?) : InventoryItemsViewState()
    data class ArticleResult(val articles: List<ArticlesForDeliveryResponseDTO>?) : InventoryItemsViewState()
    data class InventoryItems(val items: List<InventoryItem>) : InventoryItemsViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): InventoryItemsViewState()
}