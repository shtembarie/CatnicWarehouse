package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.data.repository

import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.repository.CorrectInventoryRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.correctingStock.model.CorrectInventoryItems
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 18.11.2024.
 */
class CorrectInventoryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): CorrectInventoryRepository {
    override suspend fun updateCorrectingStockItems(
        warehouseStockYardId: Int?,
        entryId: Int?,
        correctInventoryItems: CorrectInventoryItems
    ): Response<Unit> {
        return warehouseApiServices.updateCorrectingStockItems(
            warehouseStockYardId = warehouseStockYardId,
            entryId = entryId,
            correctInventoryItems = correctInventoryItems
        )
    }
}