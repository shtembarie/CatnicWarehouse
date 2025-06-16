package com.example.shared.networking.network.delivery.model.createDelivery

data class CreateDeliveryRequestModel(
    val vendorId: String?,
    val type: String,
    val warehouseCode: String,
    val customerId: String?
)
