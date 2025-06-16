package com.example.catnicwarehouse.incoming.articles.data.repository

import com.example.catnicwarehouse.incoming.articles.domain.repository.ArticleRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import retrofit2.Response
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : ArticleRepository {
    override suspend fun findDeliveryItems(deliveryId: String): Response<List<DeliveryItemRepoModel>> {
        return warehouseApiServices.findDeliveryItems(deliveryId)
    }


}