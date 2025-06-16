package com.example.catnicwarehouse.incoming.unloadingStockyards.domain.useCase

import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.repository.UnloadingStockyardsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class DefaultWarehouseStockyardsUseCase @Inject constructor(
    private val unloadingStockyardsRepository: UnloadingStockyardsRepository
) {

    operator fun invoke(
        warehouseCode: String
    ): Flow<Resource<List<WarehouseStockyardsDTO>>> = flow {

        emit(Resource.Loading())

        try {
            val response = unloadingStockyardsRepository.findDefaultWarehouseStockyards(warehouseCode=warehouseCode)
            if (response.isSuccessful) {
                val stockyards = response.body()
                stockyards?.let { emit(Resource.Success(it)) }
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