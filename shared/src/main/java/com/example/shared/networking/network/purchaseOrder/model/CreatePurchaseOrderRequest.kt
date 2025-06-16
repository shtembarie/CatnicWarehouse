package com.example.shared.networking.network.purchaseOrder.model

data class CreatePurchaseOrderRequest(
    val vendorId: String,
    val subject: String = ""
)
