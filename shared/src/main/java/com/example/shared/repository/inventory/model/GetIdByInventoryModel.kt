package com.example.shared.repository.inventory.model


data class GetIdByInventoryModel(
    val id: Int,
    val warehouseCode: String,
    val createdBy: String,
    val changedBy: String,
    val createdTimestamp: String,
    val changedTimestamp: String,
    val status: String,
    val startTime: String,
    val endTime: String?,
    val inventoryItems: List<InventoryItem>
)