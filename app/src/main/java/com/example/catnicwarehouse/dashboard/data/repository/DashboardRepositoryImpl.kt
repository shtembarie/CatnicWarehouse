package com.example.catnicwarehouse.dashboard.data.repository

import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.shared.repository.login.model.RootResponse
import com.example.shared.repository.movements.CreateMovementRequest
import com.example.shared.repository.movements.CreateMovementResponseModel
import com.example.shared.repository.movements.GetMovementsModel
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : DashboardRepository {
    override suspend fun getMovements(
        status: String?,
        onlyMyMovements: Boolean?
    ): Response<GetMovementsModel> {
       return warehouseApiServices.getMovements(status, onlyMyMovements)
    }

    override suspend fun createMovement(movementRequest: RequestBody): Response<CreateMovementResponseModel> {
        return warehouseApiServices.createMovement(movementRequest)
    }

    override suspend fun getWarehouses(): Response<List<WarehousesResponseModelItem>> {
        return warehouseApiServices.getWarehouses()
    }

    override suspend fun initialise(): Response<RootResponse> {
        return warehouseApiServices.initialize()
    }

}