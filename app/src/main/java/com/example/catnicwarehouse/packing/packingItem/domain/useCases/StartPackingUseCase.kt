package com.example.catnicwarehouse.packing.packingItem.domain.useCases

import com.example.catnicwarehouse.packing.packingItem.domain.repository.PackingItemsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class StartPackingUseCase  @Inject constructor(
    private val packingItemsRepository: PackingItemsRepository
) {

    operator fun invoke(
        id: String?,
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = packingItemsRepository.startPacking(
                id = id
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