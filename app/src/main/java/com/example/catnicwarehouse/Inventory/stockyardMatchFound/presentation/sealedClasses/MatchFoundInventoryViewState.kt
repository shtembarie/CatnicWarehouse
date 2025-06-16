package com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses

sealed class MatchFoundInventoryViewState{
    object Reset : MatchFoundInventoryViewState()
    object Empty : MatchFoundInventoryViewState()
    object Loading : MatchFoundInventoryViewState()
    data class InventoryItemUpdated(val isItemUpdated: Boolean?) : MatchFoundInventoryViewState()
    data class Error(val errorMessage: String?) : MatchFoundInventoryViewState()
}
