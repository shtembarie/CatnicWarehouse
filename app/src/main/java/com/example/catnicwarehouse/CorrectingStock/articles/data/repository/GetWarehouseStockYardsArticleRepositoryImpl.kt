package com.example.catnicwarehouse.CorrectingStock.articles.data.repository

import com.example.catnicwarehouse.CorrectingStock.articles.domain.repository.GetWarehouseStockYardsArticleRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsArticlesList
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 13.11.2024.
 */
class GetWarehouseStockYardsArticleRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetWarehouseStockYardsArticleRepository {
    override suspend fun getStockYardsArticles(
        warehouseStockYardId: Int?,
        searchTerm: String?
    ): Response<List<WarehouseStockYardsArticlesList>> {
        return warehouseApiServices.getStockYardsArticles(warehouseStockYardId)
    }
}