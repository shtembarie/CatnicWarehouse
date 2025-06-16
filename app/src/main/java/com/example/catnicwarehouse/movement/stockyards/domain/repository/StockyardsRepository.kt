package com.example.catnicwarehouse.movement.stockyards.domain.repository

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.movements.GetMovementsModel
import retrofit2.Response

interface StockyardsRepository {
    suspend fun getWarehouseStockyardsByWarehouseCode(
        warehouseCode: String?
    ): Response<ArrayList<WarehouseStockyardsDTO>>
}