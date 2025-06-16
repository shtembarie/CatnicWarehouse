package com.example.catnicwarehouse.movement.articles.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses.MatchFoundEvent
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel

sealed class ArticlesEvent {
    data class GetStockyardInventory(val stockyardId: String?) :
        ArticlesEvent()
    data class SearchArticle(val searchTerm: String): ArticlesEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): ArticlesEvent()
    object Reset: ArticlesEvent()

}
