package com.example.catnicwarehouse.inventoryNew.stockyards.data.respository

import com.example.catnicwarehouse.inventoryNew.stockyards.domain.repository.GetCurrentInventoryRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import retrofit2.Response
import javax.inject.Inject

class GetCurrentInventoryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetCurrentInventoryRepository {
    override suspend fun getCurrentInventory(warehouseCode: String?): Response<CurrentInventoryResponseModel> {
        return warehouseApiServices.getCurrentInventory(warehouseCode)
    }
}