package com.example.shared.networking.network.delivery.model.createDelivery

import com.google.gson.annotations.SerializedName

data class CreateDeliveryResponseModel(
    @SerializedName("additionalProp1") val additionalProp1: String,
    @SerializedName("additionalProp2") val additionalProp2: String,
    @SerializedName("additionalProp3") val additionalProp3: String,
)