package com.example.shared.networking.network.delivery.model.updateDeliveryItemForPurchase

data class UpdateDeliveryItemToPurchaseRequestModel(
    val articleId: String, val unitCode: String, val amount: Int
)