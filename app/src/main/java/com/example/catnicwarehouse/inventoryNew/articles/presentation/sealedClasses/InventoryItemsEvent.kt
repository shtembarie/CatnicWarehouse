package com.example.catnicwarehouse.inventoryNew.articles.presentation.sealedClasses

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent

sealed class InventoryItemsEvent{
    object Reset : InventoryItemsEvent()
    data class SearchArticle(val searchTerm: String): InventoryItemsEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): InventoryItemsEvent()
    data class InventoryItems(val id: String) : InventoryItemsEvent()

}