package com.example.catnicwarehouse.incoming.inventoryItems.data.domain.repository

import com.example.shared.repository.inventory.model.InventoryResponse
import retrofit2.Response

interface InventoryRepository {
    suspend fun findInventoryItems(
        warehouseCode:String,
        status:String
    ): Response<List<InventoryResponse>>
}