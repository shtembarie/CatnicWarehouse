package com.example.shared.repository.delivery.model.deliveryRepo

data class DeliveryItemRepoModel(
    val amount: Int,
    val articleDescription: String,
    val articleId: String,
    val articleMatchCode: String,
    val deliveryId: String,
    val id: Int,
    val unitCode: String,
    val weightInKg: Int
)