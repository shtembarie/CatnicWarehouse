package com.example.catnicwarehouse.movement.stockyards.domain.useCases

import com.example.catnicwarehouse.movement.stockyards.domain.repository.StockyardsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetWarehouseStockyardsByWarehouseCodeUseCase @Inject constructor(
    private val stockyardsRepository: StockyardsRepository
) {

    operator fun invoke(
        warehouseCode: String?,
    ): Flow<Resource<ArrayList<WarehouseStockyardsDTO>>> = flow {

        emit(Resource.Loading())

        try {
            val response = stockyardsRepository.getWarehouseStockyardsByWarehouseCode(
                warehouseCode = warehouseCode
            )

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