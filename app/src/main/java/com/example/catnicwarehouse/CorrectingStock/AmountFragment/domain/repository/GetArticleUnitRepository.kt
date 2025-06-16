package com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.repository

import com.example.shared.repository.correctingStock.model.ArticleUnitsResponse
import retrofit2.Response

/**
 * Created by Enoklit on 19.11.2024.
 */
interface GetArticleUnitRepository {
    suspend fun getArticleUnit(
        articleId: String
    ): Response<ArticleUnitsResponse>
}