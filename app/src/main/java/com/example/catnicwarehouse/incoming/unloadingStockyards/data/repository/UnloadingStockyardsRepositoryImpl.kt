package com.example.catnicwarehouse.incoming.unloadingStockyards.data.repository

import com.example.catnicwarehouse.incoming.deliveryType.domain.repository.DeliveryTypeRepository
import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.repository.UnloadingStockyardsRepository
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.warehouse.FindWarehouseDTO
import com.example.shared.networking.services.main.ApiServices
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class UnloadingStockyardsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : UnloadingStockyardsRepository {
    override suspend fun findWarehouses(searchTerm: String?): Response<List<FindWarehouseDTO>> {
        return warehouseApiServices.findWarehouses(searchTerm)
    }

    override suspend fun findDefaultWarehouseStockyards(warehouseCode: String): Response<List<WarehouseStockyardsDTO>> {
        return warehouseApiServices.getDefaultPickupAndDropZoneStockYards(warehouseCode)
    }

}