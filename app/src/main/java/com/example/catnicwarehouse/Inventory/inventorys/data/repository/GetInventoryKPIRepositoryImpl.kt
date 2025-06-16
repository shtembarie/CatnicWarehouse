package com.example.catnicwarehouse.Inventory.matchFoundStockYard.data.repository

import com.example.catnicwarehouse.Inventory.matchFoundStockYard.domain.repository.GetInventoryKPIRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoryKPI
import retrofit2.Response
import javax.inject.Inject

class GetInventoryKPIRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
    ): GetInventoryKPIRepository {
    override suspend fun getInventoryKPI(
        id:Int
    ): Response<InventoryKPI>{
        return warehouseApiServices.getInventorykpi(id)

    }

}