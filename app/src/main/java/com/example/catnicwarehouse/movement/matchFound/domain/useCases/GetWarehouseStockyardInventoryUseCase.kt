package com.example.catnicwarehouse.movement.matchFound.domain.useCases

import com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.movements.PickUpRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class PickUpArticleUseCase @Inject constructor(
    private val matchFoundRepository: MatchFoundRepository
) {

    operator fun invoke(
        id: String?,
        pickUpRequestModel: PickUpRequestModel?
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = matchFoundRepository.pickUp(
                id = id,
                pickUpRequestModel = pickUpRequestModel
            )

            if (response.isSuccessful) {
                val articles = response.body()
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