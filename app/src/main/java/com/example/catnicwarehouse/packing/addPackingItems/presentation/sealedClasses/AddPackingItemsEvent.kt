package com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses

import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel

sealed class AddPackingItemsEvent {
    data class GetItemsForPacking(
        val packingListId: String,
    ) : AddPackingItemsEvent()

    object Reset : AddPackingItemsEvent()
}