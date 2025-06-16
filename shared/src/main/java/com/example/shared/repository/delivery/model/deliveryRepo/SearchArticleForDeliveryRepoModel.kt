package com.example.shared.repository.delivery.model.deliveryRepo

data class SearchArticleForDeliveryRepoModel(
    val articleId: String,
    val matchCode: String?,
    val description: String?,
    val quantityInPurchaseOrders: String?,
    val unitCode: String?
)
