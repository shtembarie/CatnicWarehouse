package com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses

import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel

sealed class PackingItemsEvent {
    data class GetPackingItems(val id: String?) : PackingItemsEvent()
    data class StartPacking(val id: String?) : PackingItemsEvent()
    data class GetPackingListComment(val id: String?) : PackingItemsEvent()
    object Empty : PackingItemsEvent()
}