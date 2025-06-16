package com.example.catnicwarehouse.login.domain.usecase

import android.content.SharedPreferences
import com.example.catnicwarehouse.login.domain.repository.LoginRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.local.dataStore.model.UserInfo
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dataStoreManager: DataStoreManager,
    private val preferences: SharedPreferences
) {

    operator fun invoke(userName: String, password: String): Flow<Resource<Unit>> = flow {

        emit(Resource.Loading())
        try {
            val response = loginRepository.executeLogin(
                userName = userName,
                password = password
            )
            if (response.isSuccessful) {
                val loginModel = response.body()
                loginModel?.let { model ->
                    with(dataStoreManager) {
                        storeUserInfo(
                            UserInfo(
                                accesToken = model.access_token,
                                expires_in = model.expires_in,
                                token_type = model.token_type,
                                refresh_token = model.refresh_token ?: ""
                            )
                        )
                    }
                    preferences.edit().putString("token", model.access_token.trim()).apply()
                    emit(Resource.Success(Unit))
                } ?: run {
                    emit(Resource.Error("Login Failed"))
                }
            } else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}