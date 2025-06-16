package com.example.shared.networking.services.login

import com.example.shared.networking.network.login.model.LoginNetworkResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginApiService {
    @FormUrlEncoded
    @POST("/core/connect/token")
     suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String="password",
        @Field("scope") scope: String="all_claims KAM",
        @Field("client_id") client_id: String="KMO",
        @Field("client_secret") client_secret: String="secret",
    ): Response<LoginNetworkResponse>
}