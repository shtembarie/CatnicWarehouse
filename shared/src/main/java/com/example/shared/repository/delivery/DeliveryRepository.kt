package com.example.shared.repository.delivery

import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryRepositoryModel
import com.example.shared.repository.delivery.model.deliveryRepo.SearchArticleForDeliveryRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {
    fun loadDeliveryItems(
        scope: CoroutineScope, deliveryId: String
    ): Flow<DataState<List<DeliveryItemRepoModel>>>

    fun loadArticleForDelivery(
        searchTerm: String, scope: CoroutineScope
    ): Flow<DataState<List<SearchArticleForDeliveryRepoModel>>>

    fun loadScannedSearchArticle(): List<SearchArticleForDeliveryRepoModel>

    fun createDeliveryAndAutoAssignment(
        vendorId: String, scope: CoroutineScope, articleId: String, unitCode: String, amount: Int, isManualAssignment: Boolean
    ): Flow<DataState<Pair<String, String>>>

    fun createDeliveryItemAndAutoAssignment(
        scope: CoroutineScope,
        deliveryId: String,
        vendorId: String,
        articleId: String,
        unitCode: String,
        amount: Int
    ): Flow<DataState<Pair<String, String>>>

    fun createDeliveryItemWithPurchase(
        vendorId: String,
        scope: CoroutineScope,
        articleId: String,
        unitCode: String,
        amount: Int,
        purchaseOrderId: String
    ): Flow<DataState<Pair<String,String>>>

    fun updateDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        purchaseOrderItemReference: String,
        articleId: String,
        unitCode: String,
        amount: Int
    ): Flow<DataState<Boolean>>

    fun completeDelivery(scope: CoroutineScope,deliveryId: String): Flow<DataState<Boolean>>
}