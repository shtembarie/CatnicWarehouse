package com.example.catnicwarehouse.Inventory.matchFoundStockYard.domain.repository

import com.example.shared.repository.inventory.model.InventoryKPI
import retrofit2.Response

interface GetInventoryKPIRepository {
    suspend fun getInventoryKPI(
        id:Int
    ): Response<InventoryKPI>
}