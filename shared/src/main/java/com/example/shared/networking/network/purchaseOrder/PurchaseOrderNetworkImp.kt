package com.example.shared.networking.network.purchaseOrder

import com.example.shared.networking.network.purchaseOrder.model.CreatePurchaseOrderRequest
import com.example.shared.networking.network.purchaseOrder.model.CreatePurchaseOrderResponse
import com.example.shared.networking.network.purchaseOrder.model.PurchaseOrderNetworkModel
import com.example.shared.networking.services.main.ApiServices
import com.example.shared.repository.purchaseOrder.model.ValidPurchaseOrderItemRepoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PurchaseOrderNetworkImp @Inject constructor(private val apiServices: ApiServices) :
    PurchaseOrderNetwork {
    override fun loadPurchaseOrders(
        id: String, scope: CoroutineScope
    ): Flow<PurchaseOrderNetworkModel?> = callbackFlow {
        apiServices.getPurchaseOrderItems(id).enqueue(object : Callback<PurchaseOrderNetworkModel> {
            override fun onResponse(
                call: Call<PurchaseOrderNetworkModel>, response: Response<PurchaseOrderNetworkModel>
            ) {
                scope.launch {
                    send(response.body())
                }
            }

            override fun onFailure(call: Call<PurchaseOrderNetworkModel>, t: Throwable) {
                scope.launch {
                    send(null)
                }
            }
        })
        awaitClose()
    }

    override fun loadPurchaseOrdersForDelivery(
        vendorId: String, articleId: String, scope: CoroutineScope
    ): Flow<List<ValidPurchaseOrderItemRepoModel>?> = callbackFlow {
        apiServices.getValidPurchaseOrdersForDelivery(vendorId, articleId)
            .enqueue(object : Callback<List<ValidPurchaseOrderItemRepoModel>> {
                override fun onResponse(
                    call: Call<List<ValidPurchaseOrderItemRepoModel>>,
                    response: Response<List<ValidPurchaseOrderItemRepoModel>>
                ) {
                    scope.launch {
                        send(response.body()!!)
                    }
                }

                override fun onFailure(
                    call: Call<List<ValidPurchaseOrderItemRepoModel>>, t: Throwable
                ) {
                    scope.launch {
                        send(null)
                    }
                }
            })
        awaitClose()
    }

    override fun createPurchaseOrder(
        scope: CoroutineScope,
        vendorId: String,
        subject: String
    ): Flow<String> = callbackFlow {
        apiServices.createPurchaseOrder(CreatePurchaseOrderRequest(vendorId, subject))
            .enqueue(object : Callback<CreatePurchaseOrderResponse> {
                override fun onResponse(
                    call: Call<CreatePurchaseOrderResponse>, response: Response<CreatePurchaseOrderResponse>
                ) {
                    scope.launch {
                        if (response.code() in listOf(200, 201, 204)) {
                            send(response.body()!!.id)
                        } else {
                            send("")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CreatePurchaseOrderResponse>, t: Throwable
                ) {
                    scope.launch {
                        send("")
                    }
                }
            })
        awaitClose()
    }

    override fun getPurchaseOrder(scope: CoroutineScope, vendorId: String): Flow<Boolean> = callbackFlow{
        apiServices.getPurchaseOrder(vendorId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    scope.launch {
                        if (response.code() in listOf(200, 201, 204)) {
                            send(true)
                        } else {
                            send(false)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>, t: Throwable
                ) {
                    scope.launch {
                        send(false)
                    }
                }
            })
        awaitClose()
    }
}