package com.example.shared.networking.network.customer.model

import com.google.gson.annotations.SerializedName

data class SearchedCustomerDTOItem(
    @SerializedName("adR_company1")
    val company1: String?,
    val bupA_id: String?,
    @SerializedName("cuS_id")
    val customerId: String?
)