package com.example.catnicwarehouse.movement.movementList.data.repository

import com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.movement.movementList.domain.repository.MovementListRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import retrofit2.Response
import javax.inject.Inject

class MovementListRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : MovementListRepository {
    override suspend fun closeMovement(id: String?): Response<Unit> {
        return warehouseApiServices.closeMovement(id)
    }

}