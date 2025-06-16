package com.example.catnicwarehouse.incoming.deliveries.data.repository


import com.example.catnicwarehouse.incoming.deliveries.domain.repository.DeliveryRepository
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : DeliveryRepository {

    override suspend fun findDeliveries(
        date: String?,
        user: String?,
        status: String?
    ): Response<List<DeliveryResponseModel>> {
       return warehouseApiServices.findDelivery(

       )
    }

    override suspend fun getDelivery(id: String): Response<GetDeliveryResponseModel> {
        return warehouseApiServices.getDelivery(id)
    }

}