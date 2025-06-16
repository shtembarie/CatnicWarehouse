package com.example.catnicwarehouse.movement.matchFound.domain.useCases

import com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetWarehouseStockyardInventoryEntriesUseCase @Inject constructor(
    private val matchFoundRepository: MatchFoundRepository
) {

    operator fun invoke(
        id: String?,
        stockyardId: String?,
        warehouseCode:String?
    ): Flow<Resource<ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?>> = flow {

        emit(Resource.Loading())

        try {
            val response = matchFoundRepository.getWarehouseStockyardInventoryEntries(
                articleId = id,
                stockyardId = stockyardId,
                warehouseId = warehouseCode
            )

            if (response.isSuccessful) {
                val result = response.body()
                emit(Resource.Success(result))

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