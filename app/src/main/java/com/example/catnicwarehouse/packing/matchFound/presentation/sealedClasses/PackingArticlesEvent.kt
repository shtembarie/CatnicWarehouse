package com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses

import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsEvent
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel

sealed class PackingArticlesEvent {
    data class PickAmount(
        val packingListId: String?,
        val itemId: String?,
        val pickAmountRequestModel: PickAmountRequestModel?
    ) : PackingArticlesEvent()

    data class changePackedAmount(
        val packingListId: String?,
        val itemId: String?,
        val packedAmount: Int?
    ) : PackingArticlesEvent()

    data class GetPackingItems(val id: String?) : PackingArticlesEvent()

    data class GetItemsForPacking(
        val packingListId: String,
    ) : PackingArticlesEvent()

    data class GetPackingListStatus(
        val packingListId: String,
    ) : PackingArticlesEvent()

    data class GetWarehouseStockyardInventoryEntries(val articleId: String?, val stockyardId:String?, val warehouseCode:String?, val isFromUserEntry:Boolean): PackingArticlesEvent()

    data class CancelPackingList(val id: String?,val cancelPackingRequestModel: CancelPackingRequestModel) : PackingArticlesEvent()
    object GetDefaultPackingZones : PackingArticlesEvent()

    object Reset : PackingArticlesEvent()
}