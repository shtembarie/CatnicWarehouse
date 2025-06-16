package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.data.repository

import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.PostDefectiveItemsRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class PostDefectiveItemsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : PostDefectiveItemsRepository{
    override suspend fun postParams(postDefectiveArticlesModel: PostDefectiveArticlesModel?): Response<Unit> {
        return warehouseApiServices.postDefectiveItems(postDefectiveArticlesModel = postDefectiveArticlesModel)
    }

}