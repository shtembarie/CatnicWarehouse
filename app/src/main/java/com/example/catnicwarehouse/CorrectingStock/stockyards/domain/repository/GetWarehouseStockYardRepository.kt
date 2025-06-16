package com.example.catnicwarehouse.CorrectingStock.stockyards.domain.repository

import com.example.shared.repository.correctingStock.model.WarehouseStockYardsList
import retrofit2.Response

/**
 * Created by Enoklit on 07.11.2024.
 */
interface GetWarehouseStockYardRepository {
    suspend fun getWarehouseStockYards(
        warehouseCode: String? = null,
    ): Response<List<WarehouseStockYardsList>>
}