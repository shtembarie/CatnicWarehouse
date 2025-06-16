package com.example.catnicwarehouse.Inventory.stockyards.domain.repository

import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import retrofit2.Response

interface GetInventoryByIdRepository {
    suspend fun getCurrentInventory(
        warehouseCode: String
    ): Response<CurrentInventoryResponseModel>
}


