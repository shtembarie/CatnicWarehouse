package com.example.shared.repository.purchaseOrder

import com.example.shared.repository.purchaseOrder.model.PurchaseOrderRepoModel
import com.example.shared.repository.purchaseOrder.model.ValidPurchaseOrderItemRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PurchaseOrderRepository {
    fun loadPurchaseOrders(
        id: String, scope: CoroutineScope
    ): Flow<DataState<List<PurchaseOrderRepoModel>>>

    fun loadPurchaseOrdersForDelivery(
        vendorId: String, articleId: String, scope: CoroutineScope,
    ): Flow<DataState<List<ValidPurchaseOrderItemRepoModel>>>

    fun createPurchaseOrder(
        scope: CoroutineScope,
        vendorId: String,
        subject: String,
        articleId: String
    ): Flow<DataState<List<ValidPurchaseOrderItemRepoModel>>>

    fun getPurchaseOrder(
        scope: CoroutineScope,
        purchaseOrderId: String
    ): Flow<DataState<Boolean>>

}