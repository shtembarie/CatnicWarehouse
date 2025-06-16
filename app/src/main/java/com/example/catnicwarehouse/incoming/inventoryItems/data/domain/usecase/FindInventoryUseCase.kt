package com.example.catnicwarehouse.incoming.inventoryItems.data.domain.usecase

import com.example.catnicwarehouse.incoming.inventoryItems.data.domain.repository.InventoryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.inventory.model.InventoryResponse
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class FindInventoryUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository,

) {
    operator fun invoke(
        warehouseCode: String,
        status: String
    ): Flow<Resource<List<InventoryResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = inventoryRepository.findInventoryItems(
                warehouseCode = warehouseCode,
                status = status
            )
            if (response.isSuccessful) {
                val inventoryList = response.body()
                emit(Resource.Success(inventoryList ?: emptyList()))
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