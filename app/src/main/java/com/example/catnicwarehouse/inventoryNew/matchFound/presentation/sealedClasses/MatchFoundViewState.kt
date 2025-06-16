package com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses

import com.example.shared.repository.inventory.model.InventoriseItemResponseModel

sealed class MatchFoundViewState {
    object Empty : MatchFoundViewState()
    object Loading : MatchFoundViewState()
    data class Error(val errorMessage: String?) : MatchFoundViewState()
    data class ItemInventorized(val result:InventoriseItemResponseModel?,val clickedInventoryItemId:Int?) : MatchFoundViewState()

}