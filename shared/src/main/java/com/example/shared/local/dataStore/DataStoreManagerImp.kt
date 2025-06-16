package com.example.shared.local.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shared.local.dataStore.model.UserInfo
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManagerImp @Inject constructor(@ApplicationContext private val context: Context) :
    DataStoreManager {
    private val ACCESSTOKEN = stringPreferencesKey("accesToken")
    private val EXPIRESIN = intPreferencesKey("expires_in")
    private val TOKEN_TYPE = stringPreferencesKey("token_type")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val FIRST_SCAN = booleanPreferencesKey("first_scan")
    private val WAREHOUSE_KEY = stringPreferencesKey("warehouse_key")
    private val PACKING_LIST_COMMENT = stringPreferencesKey("packing_list_comment")
    override suspend fun loadUserInfo(callback: (UserInfo) -> Unit) {
        context.dataStore.data
            .map { preferences ->
                // No type safety.
                UserInfo(
                    preferences[ACCESSTOKEN] ?: "",
                    preferences[EXPIRESIN] ?: 0,
                    preferences[TOKEN_TYPE] ?: "",
                    preferences[REFRESH_TOKEN] ?: ""
                )
            }.collect {
                callback(it)
            }
    }

    override suspend fun loadAccessToken(callback: suspend (String) -> Unit) {
        context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[ACCESSTOKEN] ?: ""

            }.collect {
                callback(it)
            }
    }

    override suspend fun storeUserInfo(userInfo: UserInfo) {
        context.dataStore.edit { settings ->
            settings[ACCESSTOKEN] = userInfo.accesToken
            settings[EXPIRESIN] = userInfo.expires_in
            settings[TOKEN_TYPE] = userInfo.token_type
            settings[REFRESH_TOKEN] = userInfo.refresh_token
        }

    }

    override suspend fun isFirstScan(callback: (Boolean) -> Unit) {
        context.dataStore.data
            .map { preferences ->
                preferences[FIRST_SCAN] ?: true  // Default to true if not set
            }.collect {
                callback(it)
            }
    }

    override suspend fun setFirstScan(isFirstScan: Boolean) {
        context.dataStore.edit { settings ->
            settings[FIRST_SCAN] = isFirstScan
        }
    }

    override suspend fun savePackingListComment(comment: String) {
        context.dataStore.edit { settings ->
            settings[PACKING_LIST_COMMENT] = comment
        }
    }

    override suspend fun getPackingListComment(): String {
        return context.dataStore.data
            .map { preferences -> preferences[PACKING_LIST_COMMENT] ?: "" }
            .first() // Collect only the first value emitted
    }


    override suspend fun saveWarehouse(warehousesResponseModelItem: WarehousesResponseModelItem) {
        val warehouseJson = Gson().toJson(warehousesResponseModelItem) // Convert to JSON
        context.dataStore.edit { settings ->
            settings[WAREHOUSE_KEY] = warehouseJson // Store JSON string in DataStore
        }
    }

    override suspend fun loadWarehouse(): WarehousesResponseModelItem? {
        val warehouseJson = context.dataStore.data
            .map { preferences -> preferences[WAREHOUSE_KEY] }
            .firstOrNull()

        return warehouseJson?.let { Gson().fromJson(it, WarehousesResponseModelItem::class.java) }
    }


    override suspend fun clearDataStore() {
        context.dataStore.edit { settings ->
            settings.clear()
        }
    }

}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userInfo")