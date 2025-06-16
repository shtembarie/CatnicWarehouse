package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses

/**
 * Created by Enoklit on 04.12.2024.
 */
sealed class GetDefectiveArticlesEvent{
    data class Loading(val WarehouseCode: String) : GetDefectiveArticlesEvent()
    object Reset: GetDefectiveArticlesEvent()

    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val titleText:String): GetDefectiveArticlesEvent()

}
