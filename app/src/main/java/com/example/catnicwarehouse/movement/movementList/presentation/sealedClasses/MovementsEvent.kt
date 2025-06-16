package com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent

sealed class MovementsEvent {
    data class GetMovements(val status: String?=null, val onlyMyMovements: Boolean?) :
        MovementsEvent()
    data class SearchArticle(val searchTerm: String): MovementsEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): MovementsEvent()
    data class CloseMovement(val id: String?): MovementsEvent()
}
