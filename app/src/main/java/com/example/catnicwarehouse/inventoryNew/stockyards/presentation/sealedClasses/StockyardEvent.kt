package com.example.catnicwarehouse.inventoryNew.stockyards.presentation.sealedClasses

sealed class StockyardEvent {
    data class GetWarehouseStockyardsByWarehouseCode(val warehouseCode: String?) :
        StockyardEvent()

    data class GetWarehouseStockyardById(val id: String, val isFromUserInteraction: Boolean) :
        StockyardEvent()

    data class GetCurrentInventory(val warehouseCode: String?) : StockyardEvent()
    object Reset : StockyardEvent()
}
