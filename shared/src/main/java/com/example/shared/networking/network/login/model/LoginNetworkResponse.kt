package com.example.shared.networking.network.login.model

import com.google.gson.annotations.SerializedName

data class LoginNetworkResponse(
    @SerializedName("access_token") val access_token: String,
    @SerializedName("expires_in") val expires_in: Int,
    @SerializedName("token_type") val token_type: String,
    @SerializedName("refresh_token") val refresh_token: String?
)

