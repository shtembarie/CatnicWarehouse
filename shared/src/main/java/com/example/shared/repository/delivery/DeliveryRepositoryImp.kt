package com.example.shared.repository.delivery

import com.example.shared.networking.network.delivery.DeliveryManager
import com.example.shared.networking.network.delivery.model.createDeliveryItem.AssignDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItemForPurchase.UpdateDeliveryItemToPurchaseRequestModel
import com.example.shared.repository.delivery.dto.DeliveryNetworkToRepositoryMapper
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryRepositoryModel
import com.example.shared.repository.delivery.model.deliveryRepo.SearchArticleForDeliveryRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepositoryImp @Inject constructor(
    private val deliveryManager: DeliveryManager,
    private val deliveryNetworkToRepositoryMapper: DeliveryNetworkToRepositoryMapper
) : DeliveryRepository {
    private val foundedArticleList = arrayListOf<SearchArticleForDeliveryRepoModel>()





    override fun loadDeliveryItems(
        scope: CoroutineScope, deliveryId: String
    ): Flow<DataState<List<DeliveryItemRepoModel>>> = callbackFlow {
        deliveryManager.loadDeliveryItems(scope, deliveryId).collect { res ->
            res?.let {
                send(DataState.Success(res))
            } ?: kotlin.run {
                send(DataState.Error(Exception("failed response")))
            }
        }
    }

    override fun loadArticleForDelivery(
        searchTerm: String, scope: CoroutineScope
    ): Flow<DataState<List<SearchArticleForDeliveryRepoModel>>> = callbackFlow {
        deliveryManager.loadArticleForDeliveryDelivery(searchTerm, scope).collect { resp ->
            resp?.let {
                val res = it.map { item ->
                    SearchArticleForDeliveryRepoModel(
                        item.articleId,
                        item.matchCode,
                        item.description,
                        item.quantityInPurchaseOrders,
                        item.unitCode
                    )
                }
                foundedArticleList.clear()
                foundedArticleList.addAll(res)
                send(DataState.Success(res))

            } ?: kotlin.run {
                send(DataState.Error(Exception("failed response")))
            }
        }
        awaitClose()
    }

    override fun loadScannedSearchArticle(): List<SearchArticleForDeliveryRepoModel> =
        foundedArticleList

    override fun createDeliveryAndAutoAssignment(
        vendorId: String, scope: CoroutineScope, articleId: String, unitCode: String, amount: Int, isManualAssignment: Boolean
    ): Flow<DataState<Pair<String, String>>> = callbackFlow {
        deliveryManager.createDelivery(vendorId, scope).collect { resp ->
            resp?.let { deliveryId ->
                deliveryManager.createDeliveryItem(
                    scope, deliveryId, articleId, unitCode, amount
                ).collect { deliveryItemId ->
                    deliveryItemId?.let {
                        if(isManualAssignment) {
                            send(DataState.Success(deliveryId to deliveryItemId))
                        }
                        else {
                            deliveryManager.automaticAssignment(scope, deliveryId, deliveryItemId)
                                .collect {
                                    if (it) {
                                        send(DataState.Success(deliveryId to deliveryItemId))
                                    } else {
                                        send(DataState.Error(Exception("failed automaticAssignment")))
                                    }
                                }
                        }
                    } ?: kotlin.run {
                        send(DataState.Error(Exception("failed to create deliveryItem")))
                    }
                }

            } ?: kotlin.run {
                send(DataState.Error(Exception("failed to create delivery")))
            }

        }
        awaitClose()
    }

    override fun createDeliveryItemAndAutoAssignment(
        scope: CoroutineScope,
        deliveryId: String,
        vendorId: String,
        articleId: String,
        unitCode: String,
        amount: Int
    ): Flow<DataState<Pair<String, String>>> = callbackFlow {
        deliveryManager.createDeliveryItem(
            scope, deliveryId, articleId, unitCode, amount
        ).collect { deliveryItemId ->
            deliveryItemId?.let {
                deliveryManager.automaticAssignment(scope, deliveryId, deliveryItemId).collect {
                    if (it) {
                        send(DataState.Success(deliveryId to deliveryItemId))
                    } else {
                        send(DataState.Error(Exception("failed automaticAssignment")))
                    }
                }
            } ?: kotlin.run {
                send(DataState.Error(Exception("failed to create deliveryItem")))
            }
        }
        awaitClose()
    }

    override fun createDeliveryItemWithPurchase(
        vendorId: String,
        scope: CoroutineScope,
        articleId: String,
        unitCode: String,
        amount: Int,
        purchaseOrderId: String
    ): Flow<DataState<Pair<String, String>>> = callbackFlow {
        deliveryManager.createDelivery(vendorId, scope).collect { resp ->
            resp?.let { deliveryId ->
                deliveryManager.createDeliveryItem(
                    scope, deliveryId, articleId, unitCode, amount
                ).collect { deliveryItemId ->
                    deliveryItemId?.let {
                        deliveryManager.assignDeliveryItemToPurchase(
                            scope,
                            deliveryId,
                            deliveryItemId,
                            AssignDeliveryItemToPurchaseRequestModel(
                                purchaseOrderId, articleId, unitCode, amount
                            )
                        ).collect {
                            if (it) {
                                send(DataState.Success(deliveryId to deliveryItemId))
                            } else {
                                send(DataState.Error(Exception("failed automaticAssignment")))
                            }
                        }
                    } ?: kotlin.run {
                        send(DataState.Error(Exception("failed to create deliveryItem")))
                    }
                }

            } ?: kotlin.run {
                send(DataState.Error(Exception("failed to create delivery")))
            }

        }
        awaitClose()
    }

    override fun updateDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        purchaseOrderItemReference: String,
        articleId: String,
        unitCode: String,
        amount: Int

    ): Flow<DataState<Boolean>> = callbackFlow {
        deliveryManager.updateDeliveryItemToPurchase(
            scope,
            deliveryId,
            deliveryItemId,
            purchaseOrderItemReference,
            UpdateDeliveryItemToPurchaseRequestModel(articleId, unitCode, amount)
        ).collect { resp ->
            resp.let { value ->
                scope.launch {
                    if (value) {
                        send(DataState.Success(true))
                    } else {
                        send(DataState.Error(Exception("Can't update deliveryItem")))
                    }
                }
            }
        }
        awaitClose()
    }

    override fun completeDelivery(
        scope: CoroutineScope, deliveryId: String
    ): Flow<DataState<Boolean>> = callbackFlow {
        delay(1000)
        deliveryManager.completeDelivery(scope, deliveryId).collect { result ->
            if (result) {
                scope.launch {
                    send(DataState.Success(true))
                }
            } else {
                scope.launch {
                    send(DataState.Error(Exception("Can't complete delivery")))
                }
            }
        }
        awaitClose()
    }
}