package com.example.catnicwarehouse.incoming.matchFound.data.repository

import com.example.catnicwarehouse.incoming.matchFound.domain.repository.MatchFoundRepository
import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.SetInventoryItems
import retrofit2.Response
import javax.inject.Inject

class MatchFoundRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : MatchFoundRepository {

    override suspend fun createDeliveryItem(
        deliveryId: String,
        createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ): Response<Int> {
        return warehouseApiServices.createDeliveryItem(
            deliveryId = deliveryId,
            createDeliveryItemRequestModel = createDeliveryItemRequestModel
        )
    }

    override suspend fun bookDeliveryItem(
        deliveryId: String,
        deliveryItemId: String
    ): Response<Unit> {
        return warehouseApiServices.bookDeliveryItem(
            deliveryId = deliveryId,
            deliveryItemId = deliveryItemId
        )
    }

    override suspend fun updateDeliveryItem(
        deliveryId: String,
        deliveryItemId: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ): Response<Unit> {
        return warehouseApiServices.updateDeliveryItem(
            deliveryId = deliveryId,
            deliveryItemId = deliveryItemId,
            updateDeliveryItemRequestModel = updateDeliveryItemRequestModel
        )
    }

}