package com.example.shared.repository.purchaseOrder

import com.example.shared.networking.network.purchaseOrder.PurchaseOrderNetwork
import com.example.shared.repository.purchaseOrder.model.PurchaseOrderRepoModel
import com.example.shared.repository.purchaseOrder.model.ValidPurchaseOrderItemRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PurchaseOrderRepositoryImp @Inject constructor(private val purchaseOrderNetwork: PurchaseOrderNetwork) :
    PurchaseOrderRepository {
    override fun loadPurchaseOrders(
        id: String, scope: CoroutineScope
    ): Flow<DataState<List<PurchaseOrderRepoModel>>> = callbackFlow {
        purchaseOrderNetwork.loadPurchaseOrders(id, scope).collect { resp ->
            resp?.let {
                val res = it.items.map {
                    PurchaseOrderRepoModel(
                        it.id, it.iposition.toString(), it.description
                    )
                }
                send(DataState.Success(res))
            } ?: kotlin.run {
                send(DataState.Error(Exception("server error")))
            }
        }
        awaitClose()
    }

    override fun loadPurchaseOrdersForDelivery(
        vendorId: String, articleId: String, scope: CoroutineScope
    ): Flow<DataState<List<ValidPurchaseOrderItemRepoModel>>> = callbackFlow {
        purchaseOrderNetwork.loadPurchaseOrdersForDelivery(vendorId, articleId, scope)
            .collect { resp ->
                resp?.let { it ->
                    send(DataState.Success(it))
                } ?: kotlin.run {
                    send(DataState.Error(Exception("server error")))
                }
            }
        awaitClose()
    }

    override fun createPurchaseOrder(
        scope: CoroutineScope, vendorId: String, subject: String, articleId: String
    ): Flow<DataState<List<ValidPurchaseOrderItemRepoModel>>> = callbackFlow {
        purchaseOrderNetwork.createPurchaseOrder(scope, vendorId, subject).collect { responce ->
            loadPurchaseOrdersForDelivery(vendorId, articleId, scope).collect { resp ->
                resp?.let { it ->
                    send(resp)
                } ?: kotlin.run {
                    send(DataState.Error(Exception("server error")))
                }
            }
        }
        awaitClose()
    }

    override fun getPurchaseOrder(
        scope: CoroutineScope, purchaseOrderId: String
    ): Flow<DataState<Boolean>> = callbackFlow {
        purchaseOrderNetwork.getPurchaseOrder(scope, purchaseOrderId).collect { response ->
            send(DataState.Success(true))
        }
        awaitClose()
    }
}