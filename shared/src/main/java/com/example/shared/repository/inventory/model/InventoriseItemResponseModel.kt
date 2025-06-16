package com.example.shared.repository.inventory.model

data class InventoriseItemResponseModel(
    val conflict: Boolean,
    val conflicts: Conflicts,
    val createdInventoryItemId: Int,
    val updatedInventoryItemId: Int
)