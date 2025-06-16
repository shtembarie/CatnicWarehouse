package com.example.catnicwarehouse.movement.articles.domain.useCases

import com.example.catnicwarehouse.movement.articles.domain.repository.StockyardArticlesRepository
import com.example.catnicwarehouse.movement.stockyards.domain.repository.StockyardsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetWarehouseStockyardInventoryUseCase @Inject constructor(
    private val stockyardArticlesRepository: StockyardArticlesRepository
) {

    operator fun invoke(
        id: String?,
    ): Flow<Resource<ArrayList<WarehouseStockyardInventoryResponseModel>>> = flow {

        emit(Resource.Loading())

        try {
            val response = stockyardArticlesRepository.getWarehouseStockyardInventory(
                stockyardId = id
            )

            if (response.isSuccessful) {
                val articles = response.body()
                articles?.let { emit(Resource.Success(it)) }
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