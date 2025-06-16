package com.example.catnicwarehouse.movement.matchFound.presentation.sealedClasses

import com.example.catnicwarehouse.movement.stockyards.presentation.sealedClasses.StockyardEvent
import com.example.shared.repository.movements.PickUpRequestModel

sealed class MatchFoundEvent {
    data class PickUp(val id: String?,val pickUpRequestModel: PickUpRequestModel) : MatchFoundEvent()
    object Reset: MatchFoundEvent()
    data class GetWarehouseStockyardById(val id: String): MatchFoundEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?,val stockyardId:String?,val warehouseCode:String?): MatchFoundEvent()
}