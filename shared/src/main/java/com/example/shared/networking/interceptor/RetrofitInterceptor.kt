package com.example.shared.networking.interceptor

import android.content.Context
import com.example.shared.local.dataStore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RetrofitInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return try {
            runBlocking(Dispatchers.IO) {
                when (response.code) {
                    500, 400, 404 -> {
                        response
                    }

                    401 -> {
                        dataStoreManager.loadAccessToken {
                                dataStoreManager.clearDataStore()
                                GlobalNavigator.logout()
                        }
                        response
                    }

                    else -> {
                        response
                    }
                }
            }
        } catch (ex: Exception) {
            chain.proceed(chain.request())
        }
    }
}