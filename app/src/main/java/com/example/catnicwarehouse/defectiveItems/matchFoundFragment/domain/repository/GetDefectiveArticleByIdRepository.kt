package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository

import com.example.shared.repository.defectiveArticles.DefectivesArticlesById
import retrofit2.Response

/**
 * Created by Enoklit on 05.12.2024.
 */
interface GetDefectiveArticleByIdRepository {
    suspend fun getDefectiveArticleByIdUnit(
        id: Int? = null
    ): Response<DefectivesArticlesById>
}