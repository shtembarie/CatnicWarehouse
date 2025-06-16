package com.example.shared.networking.network.InventoryOrder

import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class InventoryOrderNetworkImp @Inject constructor(private val inventoryApiServices: WarehouseApiServices) : InventoryOrderNetwork {

        override fun loadInventoryItems(
        warehouseCode: String, scope: CoroutineScope
    ): Flow<Boolean> = callbackFlow {
        launch {
        (object : Callback<InventoryResponse> {
                override fun onResponse(
                    call: Call<InventoryResponse>, response: Response<InventoryResponse>
                ) {
                    scope.launch {
                        if (response.code() in listOf(200, 201, 204)) {
                            send(true)
                        } else {
                            send(false)
                        }
                    }
                }

                override fun onFailure(call: Call<InventoryResponse>, t: Throwable) {
                    scope.launch {
                        send(false)
                    }
                }
            })
            awaitClose()
        }
        }


}