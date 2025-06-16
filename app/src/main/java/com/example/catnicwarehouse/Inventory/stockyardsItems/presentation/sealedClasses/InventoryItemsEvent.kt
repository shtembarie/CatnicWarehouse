package com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses

import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand

sealed class InventoryItemsEvent {
    object LoadInventory : InventoryItemsEvent()
    object Reset : InventoryItemsEvent()
    data class AddInventoryArticle(
        val inventoryId: Int,
        val stockyardId: Int,
        val command: PostStockyardItemCommand
    ): InventoryItemsEvent()
    data class SearchArticle(val searchTerm: String): InventoryItemsEvent()
    data class GetWarehouseStockyardById(val id: String): InventoryItemsEvent()
}
