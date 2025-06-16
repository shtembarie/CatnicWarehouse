package com.example.catnicwarehouse.incoming.deliveryDetail.data.repository

import com.example.catnicwarehouse.incoming.articles.domain.repository.ArticleRepository
import com.example.catnicwarehouse.incoming.deliveryDetail.domain.repository.DeliveryDetailsRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class DeliveryDetailsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : DeliveryDetailsRepository {
    override suspend fun completeDeliveryWarehousing(deliveryId: String): Response<Unit> {
        return warehouseApiServices.completeDeliveryWarehousing(deliveryId = deliveryId)
    }

}