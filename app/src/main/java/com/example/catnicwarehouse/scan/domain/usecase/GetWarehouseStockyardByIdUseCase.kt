package com.example.catnicwarehouse.scan.domain.usecase

import com.example.catnicwarehouse.scan.domain.repository.ManualInputRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetWarehouseStockyardByIdUseCase  @Inject constructor(
    private val manualInputRepository: ManualInputRepository
) {

    operator fun invoke(
        id: String
    ): Flow<Resource<WarehouseStockyardsDTO>> = flow {

        emit(Resource.Loading())

        try {
            val response = manualInputRepository.getWarehouseStockyardById(id=id)
            if (response.isSuccessful) {
                val warehouseStockyardById = response.body()
                warehouseStockyardById?.let { emit(Resource.Success(it)) }
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