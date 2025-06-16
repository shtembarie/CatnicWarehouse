package com.example.catnicwarehouse.incoming.inventoryItems.data.data

import com.example.catnicwarehouse.incoming.inventoryItems.data.domain.repository.InventoryRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoryResponse
import retrofit2.Response
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): InventoryRepository {
    override suspend fun findInventoryItems(
        warehouseCode: String,
        status: String
    ): Response<List<InventoryResponse>> {
        return warehouseApiServices.getInventoryItems(warehouseCode, status)
    }

}