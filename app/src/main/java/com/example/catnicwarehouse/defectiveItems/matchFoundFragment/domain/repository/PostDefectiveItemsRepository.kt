package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository

import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import retrofit2.Response

/**
 * Created by Enoklit on 05.12.2024.
 */
interface PostDefectiveItemsRepository {
    suspend fun postParams(
        postDefectiveArticlesModel: PostDefectiveArticlesModel?
    ): Response<Unit>
}