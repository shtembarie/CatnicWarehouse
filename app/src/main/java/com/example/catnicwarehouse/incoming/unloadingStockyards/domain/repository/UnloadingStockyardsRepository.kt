package com.example.catnicwarehouse.incoming.unloadingStockyards.domain.repository

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.warehouse.FindWarehouseDTO
import retrofit2.Response

interface UnloadingStockyardsRepository {
    suspend fun findWarehouses(
        searchTerm: String?
    ): Response<List<FindWarehouseDTO>>

    suspend fun findDefaultWarehouseStockyards(
        warehouseCode: String
    ): Response<List<WarehouseStockyardsDTO>>
}