package com.example.catnicwarehouse.movement.summary.domain.repository

import com.example.shared.repository.movements.DropOffRequestModel
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import retrofit2.Response

interface MovementSummaryRepository {
    suspend fun dropOff(
        id: String?,
        dropOffRequestModel: DropOffRequestModel?
    ): Response<Unit>

}