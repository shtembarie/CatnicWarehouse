package com.example.shared.networking.network.delivery

import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.SearchArticleForDeliveryResponseModel
import com.example.shared.networking.network.delivery.model.createDeliveryItem.AssignDeliveryItemToPurchaseRequestModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItemForPurchase.UpdateDeliveryItemToPurchaseRequestModel
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface DeliveryManager {
    fun loadDelivery(scope: CoroutineScope): Flow<List<DeliveryResponseModel>?>

    fun loadDeliveryItems(
        scope: CoroutineScope,
        deliveryId: String
    ): Flow<List<DeliveryItemRepoModel>?>

    fun loadArticleForDeliveryDelivery(
        searchTerm: String,
        scope: CoroutineScope
    ): Flow<List<SearchArticleForDeliveryResponseModel>?>

    fun createDelivery(vendorId: String, scope: CoroutineScope): Flow<String?>

    fun createDeliveryItem(
        scope: CoroutineScope,
        deliveryId: String,
        articleId: String,
        unitCode: String,
        amount: Int
    ): Flow<String?>

    fun automaticAssignment(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String
    ): Flow<Boolean>

    fun assignDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        createDeliveryItemRequestModel: AssignDeliveryItemToPurchaseRequestModel
    ): Flow<Boolean>

    fun updateDeliveryItemToPurchase(
        scope: CoroutineScope,
        deliveryId: String,
        deliveryItemId: String,
        purchaseOrderItemReference: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemToPurchaseRequestModel
    ): Flow<Boolean>

    fun completeDelivery(scope: CoroutineScope,deliveryId: String): Flow<Boolean>
}