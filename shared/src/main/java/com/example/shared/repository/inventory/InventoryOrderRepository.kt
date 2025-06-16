package com.example.shared.repository.inventory

import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface InventoryOrderRepository {

    fun loadInventoryItems(
        warehouseCode: String,
        scope: CoroutineScope
    ): Flow<DataState<Boolean>>

}