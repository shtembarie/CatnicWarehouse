package com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses

import com.example.shared.repository.inventory.model.InventoryUIModel

sealed class InventoryViewState {
    object Reset : InventoryViewState()
    object Empty : InventoryViewState()
    object Loading : InventoryViewState()
    data class Inventories(val inventories: List<InventoryUIModel>?) : InventoryViewState()
    data class Error(val errorMessage: String?) : InventoryViewState()
}
