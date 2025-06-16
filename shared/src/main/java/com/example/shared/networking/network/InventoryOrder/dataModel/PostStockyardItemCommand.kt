package com.example.shared.networking.network.InventoryOrder.dataModel

data class PostStockyardItemCommand(
    val articleId: String,
    val actualUnitCode: String,
    val actualStock: Int,
    val comment: String
)
