package com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases

import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class PausePackingUseCase @Inject constructor(
    private val finalisePackingListRepository: FinalisePackingListRepository
) {

    operator fun invoke(
        id: String?
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = finalisePackingListRepository.pausePacking(
                id = id,
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