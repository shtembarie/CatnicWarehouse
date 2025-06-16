package com.example.catnicwarehouse.movement.matchFound.data.repository

import com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import retrofit2.Response
import javax.inject.Inject

class MatchFoundRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : MatchFoundRepository {
    override suspend fun pickUp(id: String?,pickUpRequestModel: PickUpRequestModel?): Response<Unit> {
        return warehouseApiServices.pickUp(id,pickUpRequestModel)
    }

    override suspend fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockyardId: String?,
        warehouseId: String?
    ): Response<ArrayList<WarehouseStockyardInventoryEntriesResponseModel>> {
        return warehouseApiServices.getWarehouseStockyardInventoryEntries(articleId,stockyardId,warehouseId)
    }
}