package com.example.catnicwarehouse.incoming.amountItem.data.repository

import com.example.catnicwarehouse.incoming.amountItem.domain.repository.AmountItemRepository
import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class AmountItemRepositoryImpl @Inject constructor(
    val warehouseApiServices: WarehouseApiServices
) : AmountItemRepository {
    override suspend fun getArticleUnits(articleId: String): Response<ArticleUnitsResponseModel> {
        return warehouseApiServices.getArticleUnits(id = articleId)
    }

}