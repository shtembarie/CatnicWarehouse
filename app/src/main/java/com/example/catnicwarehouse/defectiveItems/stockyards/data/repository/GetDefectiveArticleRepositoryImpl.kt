package com.example.catnicwarehouse.defectiveItems.stockyards.data.repository

import com.example.catnicwarehouse.defectiveItems.stockyards.domain.repository.GetDefectiveArticleRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 04.12.2024.
 */
class GetDefectiveArticleRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): GetDefectiveArticleRepository{
    override suspend fun getDefectiveArticleUnit(
        WarehouseCode: String?
    ): Response<List<DefectiveArticleList>> {
        return warehouseApiServices.getDefectiveArticles(WarehouseCode)
    }
}