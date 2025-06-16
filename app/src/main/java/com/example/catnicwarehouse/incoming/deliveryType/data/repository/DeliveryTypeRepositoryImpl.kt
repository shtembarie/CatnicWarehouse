package com.example.catnicwarehouse.incoming.deliveryType.data.repository

import com.example.catnicwarehouse.incoming.deliveryType.domain.repository.DeliveryTypeRepository
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.services.main.ApiServices
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class DeliveryTypeRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : DeliveryTypeRepository  {
    override suspend fun createDelivery(createDeliveryRequestModel: CreateDeliveryRequestModel): Response<String> {
        return warehouseApiServices.createDelivery(createDeliveryRequestModel)
    }
}