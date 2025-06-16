package com.example.catnicwarehouse.Inventory.stockyards.data.repository

import com.example.catnicwarehouse.Inventory.stockyards.domain.repository.GetInventoryByIdRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import retrofit2.Response
import javax.inject.Inject

class GetInventoryByIdRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetInventoryByIdRepository {
    override suspend fun getCurrentInventory(warehouseCode: String): Response<CurrentInventoryResponseModel> {
        return warehouseApiServices.getCurrentInventory(warehouseCode)
    }
}