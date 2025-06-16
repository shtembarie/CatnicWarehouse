package com.example.catnicwarehouse.CorrectingStock.AmountFragment.data.repository

import com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.repository.GetArticleUnitRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.correctingStock.model.ArticleUnitsResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 19.11.2024.
 */
class GetArticleUnitRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetArticleUnitRepository {
    override suspend fun getArticleUnit(articleId: String): Response<ArticleUnitsResponse> {
        return warehouseApiServices.getArticleUnit(id = articleId)
    }

}