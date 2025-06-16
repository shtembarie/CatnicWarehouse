package com.example.catnicwarehouse.incoming.deliveryDetail.domain.repository

import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import retrofit2.Response

interface DeliveryDetailsRepository {
    suspend fun completeDeliveryWarehousing(
        deliveryId: String
    ): Response<Unit>
}