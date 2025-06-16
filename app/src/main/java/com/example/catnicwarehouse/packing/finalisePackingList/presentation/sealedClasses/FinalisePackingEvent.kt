package com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses

import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel

sealed class FinalisePackingEvent {
    data class SearchArticle(val searchTerm: String): FinalisePackingEvent()
    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): FinalisePackingEvent()
    data class PausePacking(val id: String?): FinalisePackingEvent()
    data class GetPackingItems(val id: String?) : FinalisePackingEvent()
    data class FinalizePackingList(val id: String?) : FinalisePackingEvent()
    data class CancelPackingList(val id: String?,val cancelPackingRequestModel: CancelPackingRequestModel) : FinalisePackingEvent()
    object GetDefaultPackingZones : FinalisePackingEvent()
    data class GetPackingListComment(val id: String?) : FinalisePackingEvent()
    object Empty : FinalisePackingEvent()
}