package com.example.catnicwarehouse.inventoryNew.stockyards.domain.repository

import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import retrofit2.Response

interface GetCurrentInventoryRepository {
    suspend fun getCurrentInventory(
        warehouseCode: String?
    ): Response<CurrentInventoryResponseModel>
}