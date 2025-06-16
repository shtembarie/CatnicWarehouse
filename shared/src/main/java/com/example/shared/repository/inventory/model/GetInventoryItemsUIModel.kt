package com.example.shared.repository.inventory.model

data class GetInventoryItemsUIModel(
    val id: Int,
    val warehouseCode: String,
    val warehouseStockYardId: Int,
    val warehouseStockYardName: String,
    val warehouseStockYardInventoryId: Int,
    val changedBy: String,
    val changedTimestamp: String,
    val articleId: String,
    val articleDescription: String,
    val articleMatchcode: String,
    val targetStock: Int,
    val targetUnitCode: String,
    val actualStock: Int,
    val actualUnitCode: String,
    val inventoried: Boolean,
    val comment: String,
    val differenceInBaseUnit: Int,
    val baseUnitCode: String,
    val newWarehouseStockYardInventory: Boolean
)
