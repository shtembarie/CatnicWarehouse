package com.example.catnicwarehouse.incoming.deliveryType.domain.repository

import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import retrofit2.Response

interface DeliveryTypeRepository {
    suspend fun createDelivery(
        createDeliveryRequestModel: CreateDeliveryRequestModel
    ): Response<String>
}