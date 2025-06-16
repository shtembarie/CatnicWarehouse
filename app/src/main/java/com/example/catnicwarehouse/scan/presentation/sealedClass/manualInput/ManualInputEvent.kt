package com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent

sealed class ManualInputEvent{
    data class SearchArticle(val searchTerm: String): ManualInputEvent()
    data class GetWarehouseStockyardById(val id: String): ManualInputEvent()
    data class SearchWarehouseStockyards(val searchTerm: String?="",val warehouseCode:String?="",val isFromUserSearch:Boolean): ManualInputEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): ManualInputEvent()
}
