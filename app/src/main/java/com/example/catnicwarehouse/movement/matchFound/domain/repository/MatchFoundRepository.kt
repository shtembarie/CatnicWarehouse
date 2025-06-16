package com.example.catnicwarehouse.movement.matchFound.domain.repository

import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import retrofit2.Response

interface MatchFoundRepository {
    suspend fun pickUp(
        id: String?,
        pickUpRequestModel: PickUpRequestModel?
    ): Response<Unit>

    suspend fun getWarehouseStockyardInventoryEntries(
        articleId: String?,
        stockyardId:String?,
        warehouseId:String?
    ): Response<ArrayList<WarehouseStockyardInventoryEntriesResponseModel>>
}