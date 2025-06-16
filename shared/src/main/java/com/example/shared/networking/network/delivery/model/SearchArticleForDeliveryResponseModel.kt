package com.example.shared.networking.network.delivery.model

import com.google.gson.annotations.SerializedName

data class SearchArticleForDeliveryResponseModel (
    @SerializedName("articleId") val articleId: String,
    @SerializedName("matchCode") val matchCode: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("quantityInPurchaseOrders") val quantityInPurchaseOrders: String?,
    @SerializedName("unitCode") val unitCode: String?,
)
