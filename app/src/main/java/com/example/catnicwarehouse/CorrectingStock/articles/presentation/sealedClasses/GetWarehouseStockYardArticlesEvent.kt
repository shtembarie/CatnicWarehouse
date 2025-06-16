package com.example.catnicwarehouse.CorrectingStock.articles.presentation.sealedClasses

import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses.GetWarehouseStockYardEvent
import com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.sealedClasses.InventoryItemsEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent

/**
 * Created by Enoklit on 13.11.2024.
 */
sealed class GetWarehouseStockYardArticlesEvent{
    data class Loading(val warehouseEntryId: Int) : GetWarehouseStockYardArticlesEvent()
    object Reset : GetWarehouseStockYardArticlesEvent()

    data class GetWarehouseStockyardById(val id: String): GetWarehouseStockYardArticlesEvent()

}
