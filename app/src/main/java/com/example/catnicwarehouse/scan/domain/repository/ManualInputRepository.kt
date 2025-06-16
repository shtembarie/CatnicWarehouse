package com.example.catnicwarehouse.scan.domain.repository

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import retrofit2.Response

interface ManualInputRepository {
    suspend fun searchArticlesForDelivery(
        searchTerm: String
    ): Response<List<ArticlesForDeliveryResponseDTO>>


    suspend fun getWarehouseStockyardById(
        id:String
    ):Response<WarehouseStockyardsDTO>


    suspend fun searchWarehouseStockyards(
        searchTerm:String? = "",
        warehouseCode:String? = ""
    ):Response<List<WarehouseStockyardsDTO>>
}