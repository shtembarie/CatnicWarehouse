package com.example.catnicwarehouse.incoming.articles.domain.repository


import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import retrofit2.Response

interface ArticleRepository {
    suspend fun findDeliveryItems(
        deliveryId: String
    ): Response<List<DeliveryItemRepoModel>>
}