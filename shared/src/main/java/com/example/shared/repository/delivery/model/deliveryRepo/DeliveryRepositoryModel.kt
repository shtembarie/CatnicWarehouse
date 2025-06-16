package com.example.shared.repository.delivery.model.deliveryRepo

data class DeliveryRepositoryModel(
    val id: String,
    val changedTimeStamp: String,
    val changedBy: String,
    val createdTimeStamp: String,
    val createdBy: String,
    val status: String,
    val vendorId: String,
    val vendorAddressCompany1: String,
    val createdPurchaseOrderId: String,
    val weightInKg: Double
)
