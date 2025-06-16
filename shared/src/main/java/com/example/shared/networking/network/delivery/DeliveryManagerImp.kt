package com.example.shared.networking.network.delivery

import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.SearchArticleForDeliveryResponseModel
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.delivery.model.createDeliveryItem.AssignDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItemForPurchase.UpdateDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.networking.services.main.ApiServices
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
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

class DeliveryManagerImp @Inject constructor(private val apiService: ApiServices) :
    DeliveryManager {
    override fun loadDelivery(
        scope: CoroutineScope
    ): Flow<List<DeliveryResponseModel>?> = callbackFlow {
        awaitClose()
    }

    override fun loadDeliveryItems(
        scope: CoroutineScope, deliveryId: String
    ): Flow<List<DeliveryItemRepoModel>?> = callbackFlow {
        apiService.loadDeliveryItems(deliveryId)
            .enqueue(object : Callback<List<DeliveryItemRepoModel>> {
                override fun onResponse(
                    call: Call<List<DeliveryItemRepoModel>>,
                    response: Response<List<DeliveryItemRepoModel>>
                ) {
                    scope.launch {
                        send(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<DeliveryItemRepoModel>>, t: Throwable) {
                    scope.launch {
                        send(null)
                    }
                }
            })
        awaitClose()
    }

    override fun loadArticleForDeliveryDelivery(
        searchTerm: String, scope: CoroutineScope
    ): Flow<List<SearchArticleForDeliveryResponseModel>?> = callbackFlow {
        apiService.loadDeliveryForArticle(searchTerm)
            .enqueue(object : Callback<List<SearchArticleForDeliveryResponseModel>> {
                override fun onResponse(
                    call: Call<List<SearchArticleForDeliveryResponseModel>>,
                    response: Response<List<SearchArticleForDeliveryResponseModel>>
                ) {
                    scope.launch {
                        send(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<List<SearchArticleForDeliveryResponseModel>>, t: Throwable
                ) {
                    scope.launch {
                        send(null)
                    }
                }
            })
        awaitClose()
    }

    override fun createDelivery(
        vendorId: String, scope: CoroutineScope
    ): Flow<String?> = callbackFlow {
//        apiService.createDelivery(CreateDeliveryRequestModel(vendorId))
//            .enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>, response: Response<ResponseBody>
//                ) {
//                    scope.launch {
//                        val t = response.body()!!
//                        send(t.string())
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<ResponseBody>, t: Throwable
//                ) {
//                    scope.launch {
//                        send(null)
//                    }
//                }
//            })
        awaitClose()
    }

    override fun createDeliveryItem(
        scope: CoroutineScope, deliveryId: String, articleId: String, unitCode: String, amount: Int
    ): Flow<String?> = callbackFlow {
//        apiService.createDeliveryItem(
//            deliveryId, CreateDeliveryItemRequestModel(articleId, unitCode, amount)
//        ).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>, response: Response<ResponseBody>
//            ) {
//                scope.launch {
//                    val t = response.body()!!
//                    send(t.string())
//                }
//            }
//
//            override fun onFailure(
//                call: Call<ResponseBody>, t: Throwable
//            ) {
//                scope.launch {
//                    send(null)
//                }
//            }
//        })
        awaitClose()
    }

    override fun automaticAssignment(
        scope: CoroutineScope, deliveryId: String, deliveryItemId: String
    ): Flow<Boolean> = callbackFlow {
        apiService.automaticAssignment(deliveryId, deliveryItemId)
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

    override fun assignDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        createDeliveryItemRequestModel: AssignDeliveryItemToPurchaseRequestModel
    ): Flow<Boolean> = callbackFlow {
        apiService.assignDeliveryItemToPurchase(
            deliveryId, deliveryItemId, createDeliveryItemRequestModel
        ).enqueue(object : Callback<ResponseBody> {
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

    override fun updateDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        purchaseOrderItemReference: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemToPurchaseRequestModel
    ): Flow<Boolean> = callbackFlow {
        apiService.updateDeliveryItemToPurchase(
            deliveryId, deliveryItemId, purchaseOrderItemReference, updateDeliveryItemRequestModel
        ).enqueue(object : Callback<ResponseBody> {
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

    override fun completeDelivery(scope: CoroutineScope, deliveryId: String): Flow<Boolean> =
        callbackFlow {
            apiService.completeDelivery(
                deliveryId
            ).enqueue(object : Callback<ResponseBody> {
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