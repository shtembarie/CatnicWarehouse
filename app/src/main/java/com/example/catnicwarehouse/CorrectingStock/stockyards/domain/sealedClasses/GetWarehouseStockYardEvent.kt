package com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses

import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdEvent

/**
 * Created by Enoklit on 07.11.2024.
 */
sealed class GetWarehouseStockYardEvent{
    data class Loading(val warehouseCode: String) : GetWarehouseStockYardEvent()
    object Reset : GetWarehouseStockYardEvent()

    data class SearchArticle(val searchTerm: String): GetWarehouseStockYardEvent()
    data class GetWarehouseStockyardById(val id: String) : GetWarehouseStockYardEvent()

}
