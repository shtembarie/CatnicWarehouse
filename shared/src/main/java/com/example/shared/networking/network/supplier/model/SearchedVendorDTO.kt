package com.example.shared.networking.network.supplier.model

import com.google.gson.annotations.SerializedName

data class SearchedVendorDTO(
    @SerializedName("vendorId") val vendorId: String?,
    @SerializedName("businessPartnerCode") val businessPartnerCode: String?,
    @SerializedName("company1") val company1: String?,
    @SerializedName("mainVendor")val mainVendor: Boolean?
)
