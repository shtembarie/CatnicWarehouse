package com.example.shared.local.dataStore

import com.example.shared.local.dataStore.model.UserInfo
import com.example.shared.repository.dashboard.WarehousesResponseModelItem

interface DataStoreManager {
    suspend fun loadUserInfo(callback: (UserInfo) -> Unit)
    suspend fun loadAccessToken(callback: suspend (String) -> Unit)
    suspend fun storeUserInfo(userInfo: UserInfo)

    suspend fun isFirstScan(callback: (Boolean) -> Unit)
    suspend fun setFirstScan(isFirstScan: Boolean)
    suspend fun saveWarehouse(warehousesResponseModelItem: WarehousesResponseModelItem)
    suspend fun loadWarehouse(): WarehousesResponseModelItem?

    suspend fun getPackingListComment():String
    suspend fun savePackingListComment(comment: String)
    suspend fun clearDataStore()

}