package com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses


sealed class GetInventoryByIdEvent{
    data class LoadCurrentInventory(val warehouseCode: String) : GetInventoryByIdEvent()
    object Reset : GetInventoryByIdEvent()
    data class SearchArticle(val searchTerm: String): GetInventoryByIdEvent()
    data class GetWarehouseStockyardById(val id: String) : GetInventoryByIdEvent()

}
