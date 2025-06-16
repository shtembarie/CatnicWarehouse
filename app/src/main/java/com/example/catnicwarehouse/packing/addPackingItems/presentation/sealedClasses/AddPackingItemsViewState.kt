package com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses

import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem

sealed class AddPackingItemsViewState {
    object Reset : AddPackingItemsViewState()
    object Empty : AddPackingItemsViewState()
    object Loading : AddPackingItemsViewState()
    data class Error(val errorMessage: String?) : AddPackingItemsViewState()
    data class GetItemsForPackingResponse(val itemsForPackingItems: List<GetItemsForPackingResponseModelItem>?) : AddPackingItemsViewState()
}