package com.example.catnicwarehouse.incoming.unloadingStockyards.domain.useCase

import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.repository.UnloadingStockyardsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.warehouse.FindWarehouseDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class FindWarehousesUseCase @Inject constructor(
    private val unloadingStockyardsRepository: UnloadingStockyardsRepository
) {

    operator fun invoke(
        searchTerm: String?=null
    ): Flow<Resource<List<FindWarehouseDTO>>> = flow {

        emit(Resource.Loading())

        try {
            val response = unloadingStockyardsRepository.findWarehouses(searchTerm=searchTerm)
            if (response.isSuccessful) {
                val warehouses = response.body()
                warehouses?.let { emit(Resource.Success(it)) }
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