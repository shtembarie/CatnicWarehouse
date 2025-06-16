package com.example.catnicwarehouse.dashboard.domain.useCase

import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.shared.repository.movements.GetMovementsModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetMovementsUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    operator fun invoke(
        status: String?,
        onlyMyMovements: Boolean?
    ): Flow<Resource<GetMovementsModel>> = flow {

        emit(Resource.Loading())

        try {
            val response = dashboardRepository.getMovements(
                status = status,
                onlyMyMovements = onlyMyMovements
            )

            if (response.isSuccessful) {
                val myMovements = response.body()
                myMovements?.let { emit(Resource.Success(it)) }
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