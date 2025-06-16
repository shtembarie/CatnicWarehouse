package com.example.catnicwarehouse.inventoryNew.articles.data.repository

import com.example.catnicwarehouse.inventoryNew.articles.domain.repository.InventoryItemRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoryItem
import retrofit2.Response
import javax.inject.Inject

class InventoryItemRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): InventoryItemRepository {
    override suspend fun getInventoryItems(
        id:String,
    ): Response<List<InventoryItem>>{
        return warehouseApiServices.getInventoryItems(id)
    }
}