package com.example.catnicwarehouse.movement.summary.domain.usecase

import com.example.catnicwarehouse.movement.summary.domain.repository.MovementSummaryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.movements.DropOffRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class DropOffUseCase @Inject constructor(
    private val movementSummaryRepository: MovementSummaryRepository
) {

    operator fun invoke(
        id: String?,
        dropOffRequestModel: DropOffRequestModel?
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = movementSummaryRepository.dropOff(
                id = id,
                dropOffRequestModel = dropOffRequestModel
            )

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}