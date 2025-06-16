package com.example.catnicwarehouse.incoming.matchFound.domain.repository

import com.example.catnicwarehouse.incoming.articles.presentation.adapter.ArticlesAdapterListInteraction
import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.repository.inventory.model.SetInventoryItems
import retrofit2.Response

interface MatchFoundRepository {

    suspend fun createDeliveryItem(
        deliveryId: String,
        createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ): Response<Int>
    suspend fun bookDeliveryItem(
        deliveryId: String,
        deliveryItemId: String,
    ): Response<Unit>

    suspend fun updateDeliveryItem(
        deliveryId: String,
        deliveryItemId: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ): Response<Unit>
}