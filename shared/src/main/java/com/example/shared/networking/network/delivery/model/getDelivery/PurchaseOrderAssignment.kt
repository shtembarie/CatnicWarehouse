package com.example.shared.networking.network.delivery.model.getDelivery

data class PurchaseOrderAssignment(
    val amount: Int,
    val purchaseOrderId: String,
    val purchaseOrderItemPosition: String,
    val purchaseOrderItemReference: String,
    val unitCode: String
)