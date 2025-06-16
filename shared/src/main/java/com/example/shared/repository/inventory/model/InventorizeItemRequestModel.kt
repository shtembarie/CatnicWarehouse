package com.example.shared.repository.inventory.model

data class InventorizeItemRequestModel(
    val actualStock: Int,
    val actualUnitCode: String,
    val addInventory: Boolean?=null,
    val articleId: String,
    val comment: String,
    val inventoryItemId: Int?=null,
    val overrideInventory: Boolean?=null,
    val warehouseStockYardId: Int
)