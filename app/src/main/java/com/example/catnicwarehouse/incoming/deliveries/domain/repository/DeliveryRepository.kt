package com.example.catnicwarehouse.incoming.deliveries.domain.repository

import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel
import retrofit2.Response

interface DeliveryRepository {
    suspend fun findDeliveries(
        date: String?=null,
        user: String?=null,
        status: String?=null
    ): Response<List<DeliveryResponseModel>>

    suspend fun getDelivery(
        id: String
    ): Response<GetDeliveryResponseModel>
}