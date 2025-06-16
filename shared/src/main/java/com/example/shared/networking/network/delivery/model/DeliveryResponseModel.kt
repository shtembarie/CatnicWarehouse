package com.example.shared.networking.network.delivery.model

import com.google.gson.annotations.SerializedName

data class DeliveryResponseModel(
    @SerializedName("id") val id: String,
    @SerializedName("changedTimeStamp") val changedTimeStamp: String?,
    @SerializedName("changedBy") val changedBy: String?,
    @SerializedName("createdTimeStamp") val createdTimeStamp: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("vendorId") val vendorId: String?,
    @SerializedName("vendorAddressCompany1") val vendorAddressCompany1: String?,
    @SerializedName("createdPurchaseOrderId") val createdPurchaseOrderId: String?,
    @SerializedName("weightInKg") val weightInKg: Double?,
    @SerializedName("type") val type: String?,
    @SerializedName("customerId") val customerId:String?,
    @SerializedName("Customer_address_company1") val customerAddressCompany1: String?,
    @SerializedName("warehouseCode") val warehouseCode: String?,



)
