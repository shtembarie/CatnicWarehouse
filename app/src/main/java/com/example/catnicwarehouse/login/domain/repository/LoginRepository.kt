package com.example.catnicwarehouse.login.domain.repository

import com.example.shared.networking.network.login.model.LoginNetworkResponse
import retrofit2.Response

interface LoginRepository {
    suspend fun checkConnectedUser(callback:(Boolean)->Unit)
    suspend fun executeLogin(userName:String,password: String): Response<LoginNetworkResponse>
}