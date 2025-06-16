package com.example.catnicwarehouse.CorrectingStock.stockyards.data.repository

import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.repository.GetWarehouseStockYardRepository
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsList
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 07.11.2024.
 */
class GetWarehouseStockYardRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetWarehouseStockYardRepository {
     override suspend fun getWarehouseStockYards(
        warehouseCode: String?,
    ): Response<List<WarehouseStockYardsList>> {
        return warehouseApiServices.getWarehouseStockYards(warehouseCode)
    }
}