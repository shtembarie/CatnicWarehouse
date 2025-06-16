package com.example.shared.networking.network.purchaseOrder

import com.example.shared.networking.network.purchaseOrder.model.PurchaseOrderNetworkModel
import com.example.shared.repository.purchaseOrder.model.ValidPurchaseOrderItemRepoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PurchaseOrderNetwork {
    fun loadPurchaseOrders(id: String, scope: CoroutineScope): Flow<PurchaseOrderNetworkModel?>
    fun loadPurchaseOrdersForDelivery(
        vendorId: String, articleId: String, scope: CoroutineScope
    ): Flow<List<ValidPurchaseOrderItemRepoModel>?>

    fun createPurchaseOrder(scope: CoroutineScope, vendorId: String, subject: String): Flow<String>
    fun getPurchaseOrder(scope: CoroutineScope, vendorId: String): Flow<Boolean>
}