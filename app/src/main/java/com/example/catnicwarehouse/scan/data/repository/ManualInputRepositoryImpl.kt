package com.example.catnicwarehouse.scan.data.repository


import com.example.catnicwarehouse.scan.domain.repository.ManualInputRepository
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class ManualInputRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
)  : ManualInputRepository {
    override suspend fun searchArticlesForDelivery(searchTerm: String): Response<List<ArticlesForDeliveryResponseDTO>> {
        return warehouseApiServices.getArticlesForDelivery(searchTerm)
    }

    override suspend fun getWarehouseStockyardById(id: String): Response<WarehouseStockyardsDTO> {
        return warehouseApiServices.getWarehouseStockyardById(id)
    }

    override suspend fun searchWarehouseStockyards(
        searchTerm: String?,
        warehouseCode: String?
    ): Response<List<WarehouseStockyardsDTO>> {
        return warehouseApiServices.searchWarehouseStockyards(searchTerm,warehouseCode)
    }
}