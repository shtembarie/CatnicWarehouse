package com.example.shared.networking.network.delivery.model.createDeliveryItem

data class AssignDeliveryItemToPurchaseRequestModel(
    val purchaseOrderId: String, val articleId: String, val unitCode: String, val amount: Int
)