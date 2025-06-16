package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.repository

import com.example.shared.repository.correctingStock.model.CorrectInventoryItems
import retrofit2.Response

/**
 * Created by Enoklit on 18.11.2024.
 */
interface CorrectInventoryRepository {
    suspend fun updateCorrectingStockItems(
        warehouseStockYardId: Int?,
        entryId: Int?,
        correctInventoryItems: CorrectInventoryItems
    ): Response<Unit>
}