package com.example.catnicwarehouse.dashboard.domain.useCase

import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.shared.repository.movements.CreateMovementRequest
import com.example.shared.repository.movements.CreateMovementResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class CreateMovementUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    operator fun invoke(
        createMovementRequest: CreateMovementRequest? = null
    ): Flow<Resource<CreateMovementResponseModel>> = flow {

        emit(Resource.Loading())

        // Convert the request to JSON string or use "{}" if null
        val jsonPayload = createMovementRequest?.let { request ->
            // Serialize the CreateMovementRequest to JSON
            JSONObject().apply {
                request.articleId?.let { put("articleId", it) }
                request.unitCode?.let { put("unitCode", it) }
                put("amount", request.amount)
                put("inventoryEntryId", request.inventoryEntryId)
            }.toString()
        } ?: "{}"

        // Create RequestBody
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonPayload)
        try {
            val response = dashboardRepository.createMovement(
               body = requestBody
            )

            if (response.isSuccessful) {
                val movementId = response.body()
                movementId?.let { emit(Resource.Success(it)) }
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