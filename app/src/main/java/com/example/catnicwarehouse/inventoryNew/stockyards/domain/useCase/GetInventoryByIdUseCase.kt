package com.example.catnicwarehouse.inventoryNew.stockyards.domain.useCase

import com.example.catnicwarehouse.inventoryNew.stockyards.domain.repository.GetCurrentInventoryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentInventoryUseCase @Inject constructor(
    private val getCurrentInventoryRepository: GetCurrentInventoryRepository
    ) {
    fun getCurrentInventory(warehouseCode:String?): Flow<Resource<CurrentInventoryResponseModel>> = flow {
        emit(Resource.Loading())
        try {
            val response = getCurrentInventoryRepository.getCurrentInventory(warehouseCode)
            if (response.isSuccessful) {
                val currentInventory = response.body()
                currentInventory?.let { inventory ->
                    emit(Resource.Success(inventory))
                }
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