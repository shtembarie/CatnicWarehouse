package com.example.catnicwarehouse.movement.stockyards.data.repository

import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.catnicwarehouse.movement.stockyards.domain.repository.StockyardsRepository
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class StockyardsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : StockyardsRepository {
    override suspend fun getWarehouseStockyardsByWarehouseCode(warehouseCode: String?): Response<ArrayList<WarehouseStockyardsDTO>> {
        return warehouseApiServices.getWarehouseStockyardsByWarehouseCode(warehouseCode)
    }

}