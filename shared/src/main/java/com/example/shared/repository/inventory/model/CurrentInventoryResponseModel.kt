package com.example.shared.repository.inventory.model


data class CurrentInventoryResponseModel(
    val id: Int,
    val warehouseCode: String,
    val createdBy: String,
    val changedBy: String,
    val createdTimestamp: String,
    val changedTimestamp: String,
    val status: String,
    val startTime: String,
    val endTime: String?,
    val inventoryItems: List<InventoryItem>,
    val warehouseStockYards: List<CurrentInventoryWarehouseStockYardsModel>,
    val warehouseName: String,
    val necessaryBookings: List<NecessaryBooking>
)