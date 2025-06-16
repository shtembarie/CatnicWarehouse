package com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses

import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel

sealed class PackingItemsViewState {
    object Reset : PackingItemsViewState()
    object Empty : PackingItemsViewState()
    object Loading : PackingItemsViewState()
    data class Error(val errorMessage: String?) : PackingItemsViewState()
    data class GetPackingItemsResult(val packingItems: PackingItemsModel?) : PackingItemsViewState()
    data class StartPackingResult(val isPackingStarted: Boolean?) : PackingItemsViewState()
    data class GetPackingListComment(val comment: String?) : PackingItemsViewState()
}