package com.example.shared.repository.inventory.model

data class InventoryItemData(
    val id: Int,
    val warehouseStockYardId: Int,
    val changedTimestamp: String,
    val changedBy: String,
    val actualStock: Int,
    val warehouseCode: String,
    val warehouseStockYardName: String,
    val warehouseStockYardInventoryId: Int,
    val articleId: String,
    val articleDescription: String,
    val articleMatchcode: String,
    val targetStock: Float,
    val targetUnitCode: String,
    val actualUnitCode: String,
    val inventoried: Boolean,
    val comment: String,
    val differenceInBaseUnit: Float,
    val baseUnitCode: String,
    val newWarehouseStockYardInventory: Boolean
){
    fun toInventoryItem(): InventoryItem {
        return InventoryItem(
            id,
            warehouseStockYardId,
            changedTimestamp,
            changedBy,
            actualStock,
            warehouseStockYardName,
            warehouseCode,
            warehouseStockYardInventoryId,
            articleId,
            articleDescription,
            articleMatchcode,
            targetStock,
            targetUnitCode,
            actualUnitCode,
            inventoried,
            comment,
            differenceInBaseUnit,
            baseUnitCode,
            newWarehouseStockYardInventory)
    }
}
