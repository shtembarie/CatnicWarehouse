package com.example.catnicwarehouse.packing.finalisePackingList.domain.useCases

import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetDefaultPackingZonesUseCase @Inject constructor(
    private val finalisePackingListRepository: FinalisePackingListRepository
) {

    operator fun invoke(
        warehouseCode: String?
    ): Flow<Resource<List<DefaultPackingZoneResultModel>?>> = flow {

        emit(Resource.Loading())

        try {
            val response = finalisePackingListRepository.getDefaultPackingZones(
                warehouseCode=warehouseCode
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