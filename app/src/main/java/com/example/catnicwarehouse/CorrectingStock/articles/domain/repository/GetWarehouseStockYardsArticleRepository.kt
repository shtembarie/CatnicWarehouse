package com.example.catnicwarehouse.CorrectingStock.articles.domain.repository

import com.example.shared.repository.correctingStock.model.WarehouseStockYardsArticlesList
import retrofit2.Response

/**
 * Created by Enoklit on 13.11.2024.
 */
interface GetWarehouseStockYardsArticleRepository {
    suspend fun getStockYardsArticles(
        warehouseStockYardId: Int?,
        searchTerm: String? = null
    ): Response<List<WarehouseStockYardsArticlesList>>
}