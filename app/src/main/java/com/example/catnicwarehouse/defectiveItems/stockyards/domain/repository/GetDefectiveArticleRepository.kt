package com.example.catnicwarehouse.defectiveItems.stockyards.domain.repository

import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import retrofit2.Response

/**
 * Created by Enoklit on 04.12.2024.
 */
interface GetDefectiveArticleRepository {
    suspend fun getDefectiveArticleUnit(
        WarehouseCode: String? = null
    ): Response<List<DefectiveArticleList>>
}