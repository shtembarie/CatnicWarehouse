package com.example.catnicwarehouse.movement.articles.domain.repository

import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import retrofit2.Response

interface StockyardArticlesRepository {
    suspend fun getWarehouseStockyardInventory(
        stockyardId: String?
    ): Response<ArrayList<WarehouseStockyardInventoryResponseModel>>
}