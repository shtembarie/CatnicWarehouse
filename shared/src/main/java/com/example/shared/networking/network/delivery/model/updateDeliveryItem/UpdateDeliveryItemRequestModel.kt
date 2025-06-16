package com.example.shared.networking.network.delivery.model.updateDeliveryItem

data class UpdateDeliveryItemRequestModel(
    val amount: Float=1.0f,
    val articleId: String?,
    val comment: String?,
    val defectiveAmount: Int =0,
    val defectiveUnitCode: String?,
    val reason: String?,
    val unitCode: String?,
    val warehouseStockYardId: Int=0
)