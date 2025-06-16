package com.example.catnicwarehouse.incoming.amountItem.domain.repository

import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import retrofit2.Response

interface AmountItemRepository {
    suspend fun getArticleUnits(
        articleId: String,
    ): Response<ArticleUnitsResponseModel>
}