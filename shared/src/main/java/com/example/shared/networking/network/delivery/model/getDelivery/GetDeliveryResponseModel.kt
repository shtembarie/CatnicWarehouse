package com.example.shared.networking.network.delivery.model.getDelivery

import com.google.gson.annotations.SerializedName

data class GetDeliveryResponseModel(
    val changedTimestamp: String,
    val createdPurchaseOrderId: String,
    val customerAddressCompany1: String,
    val customerId: String,
    val id: String,
    @SerializedName("items")
    val articleItems: List<ArticleItem>,
    val note: String?,
    val status: String,
    val type: String,
    val vendorAddressCompany1: String,
    val vendorId: String
)