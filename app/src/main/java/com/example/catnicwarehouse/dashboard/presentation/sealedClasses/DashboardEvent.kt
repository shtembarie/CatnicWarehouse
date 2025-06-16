package com.example.catnicwarehouse.dashboard.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemEvent
import com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses.InventoryEvent
import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.shared.repository.movements.CreateMovementRequest

sealed class DashboardEvent {
    data class GetMovements(val status: String?=null, val onlyMyMovements: Boolean?) :
        DashboardEvent()

    data class CreateMovement(val createMovementRequest: CreateMovementRequest?) :
        DashboardEvent()

    object GetWarehouses : DashboardEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): DashboardEvent()
    data class LoadInventory(val warehouseCode: String?) : DashboardEvent()
    data class SearchArticle(val searchTerm: String): DashboardEvent()
    data class GetWarehouseStockyardById(val id: String): DashboardEvent()
    object Reset: DashboardEvent()
    object GetRights: DashboardEvent()
}

