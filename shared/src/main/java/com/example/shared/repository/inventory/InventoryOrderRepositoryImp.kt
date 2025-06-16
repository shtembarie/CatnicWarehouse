package com.example.shared.repository.inventory

import com.example.shared.networking.network.InventoryOrder.InventoryOrderNetwork
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class InventoryOrderRepositoryImp @Inject constructor(private val inventoryOrderNetwork: InventoryOrderNetwork):
 InventoryOrderRepository {

        override fun loadInventoryItems(
            warehouseCode: String, scope: CoroutineScope
    ): Flow<DataState<Boolean>> = callbackFlow {
        inventoryOrderNetwork.loadInventoryItems(warehouseCode, scope).collect { resp ->
            send(DataState.Success(true))
        }
        awaitClose()
    }
}