package com.example.catnicwarehouse.movement.articles.data.repository

import com.example.catnicwarehouse.movement.articles.domain.repository.StockyardArticlesRepository
import com.example.catnicwarehouse.movement.stockyards.domain.repository.StockyardsRepository
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import retrofit2.Response
import javax.inject.Inject

class StockyardArticlesRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : StockyardArticlesRepository {
    override suspend fun getWarehouseStockyardInventory(stockyardId: String?): Response<ArrayList<WarehouseStockyardInventoryResponseModel>> {
        return warehouseApiServices.getWarehouseStockyardInventory(id=stockyardId)
    }


}