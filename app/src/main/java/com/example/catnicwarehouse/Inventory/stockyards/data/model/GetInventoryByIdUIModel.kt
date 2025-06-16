package com.example.catnicwarehouse.Inventory.stockyards.data.model

import com.example.shared.repository.inventory.model.InventoryItem

data class GetInventoryByIdUIModel(
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
