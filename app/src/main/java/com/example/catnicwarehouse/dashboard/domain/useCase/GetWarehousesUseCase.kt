package com.example.catnicwarehouse.dashboard.domain.useCase

import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetWarehousesUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    operator fun invoke(): Flow<Resource<List<WarehousesResponseModelItem>>> = flow {

        emit(Resource.Loading())

        try {
            val response = dashboardRepository.getWarehouses()

            if (response.isSuccessful) {
                val warehouses = response.body()
                warehouses?.let { emit(Resource.Success(it)) }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    errorBody?.let { JSONObject(it) }
                } catch (e: JSONException) {
                    null
                }

                val errorMessage = if (errorResponse?.has("error") == true) {
                    errorResponse.getString("error")
                } else {
                    "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}