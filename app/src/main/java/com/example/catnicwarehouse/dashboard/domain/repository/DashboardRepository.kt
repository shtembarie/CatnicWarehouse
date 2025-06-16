package com.example.catnicwarehouse.dashboard.domain.repository

import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.shared.repository.login.model.RootResponse
import com.example.shared.repository.movements.CreateMovementRequest
import com.example.shared.repository.movements.CreateMovementResponseModel
import com.example.shared.repository.movements.GetMovementsModel
import okhttp3.RequestBody
import retrofit2.Response

interface DashboardRepository {
    suspend fun getMovements(
        status: String?,
        onlyMyMovements: Boolean?
    ): Response<GetMovementsModel>

    suspend fun createMovement(body: RequestBody): Response<CreateMovementResponseModel>
    suspend fun getWarehouses(): Response<List<WarehousesResponseModelItem>>
    suspend fun initialise():Response<RootResponse>
}