package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.data.repository

import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.GetDefectiveArticleByIdRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.defectiveArticles.DefectivesArticlesById
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class GetDefectiveArticleByIdRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetDefectiveArticleByIdRepository {
    override suspend fun getDefectiveArticleByIdUnit(id: Int?): Response<DefectivesArticlesById> {
        return warehouseApiServices.getDefectiveArticlesById(id)
    }
}