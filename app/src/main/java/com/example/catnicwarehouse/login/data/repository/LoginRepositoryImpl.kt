package com.example.catnicwarehouse.login.data.repository

import android.content.SharedPreferences
import com.example.catnicwarehouse.login.domain.repository.LoginRepository
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.networking.network.login.model.LoginNetworkResponse
import com.example.shared.networking.services.login.LoginApiService



import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val dataStoreManager: DataStoreManager,
) : LoginRepository {

    override suspend fun checkConnectedUser(callback: (Boolean) -> Unit) {
        dataStoreManager.loadUserInfo {
            callback(it.accesToken.isNotEmpty())
        }
    }

    override suspend fun executeLogin(
        userName: String,
        password: String
    ): Response<LoginNetworkResponse> {
        return loginApiService.login(
            userName = userName,
            password = password
        )
    }

}