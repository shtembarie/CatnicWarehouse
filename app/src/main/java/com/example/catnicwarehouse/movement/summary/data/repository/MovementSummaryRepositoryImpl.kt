package com.example.catnicwarehouse.movement.summary.data.repository

import com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.movement.summary.domain.repository.MovementSummaryRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.movements.DropOffRequestModel
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import retrofit2.Response
import javax.inject.Inject

class MovementSummaryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : MovementSummaryRepository {
    override suspend fun dropOff(
        id: String?,
        dropOffRequestModel: DropOffRequestModel?
    ): Response<Unit> {
        return warehouseApiServices.dropOff(id = id,dropOffFRequestModel = dropOffRequestModel)
    }

}