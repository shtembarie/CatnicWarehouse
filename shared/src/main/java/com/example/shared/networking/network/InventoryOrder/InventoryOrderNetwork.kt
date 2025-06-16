package com.example.shared.networking.network.InventoryOrder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface InventoryOrderNetwork {

    fun loadInventoryItems(warehouseCode:String, scope: CoroutineScope): Flow<Boolean>
}