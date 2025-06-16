package com.example.catnicwarehouse.packing.packingList.domain.useCases

import com.example.catnicwarehouse.packing.packingList.domain.repository.PackingListRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetPackingListUseCase @Inject constructor(
    private val packingListRepository: PackingListRepository
) {

    operator fun invoke(
        id: String?,
    ): Flow<Resource<PackingModelItem?>> = flow {

        emit(Resource.Loading())

        try {
            val response = packingListRepository.getPackingList(
                id = id,
            )

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
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