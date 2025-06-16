package com.example.shared.repository.inventory.model

data class Conflicts(
    val alreadyInventoried: Boolean,
    val articleId: String,
    val duplicate: Boolean,
    val inventoryItems: List<InventoryItem>,
    val warehouseStockYardId: Int
)