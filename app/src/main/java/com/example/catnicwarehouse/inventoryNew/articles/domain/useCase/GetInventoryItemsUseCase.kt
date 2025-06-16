package com.example.catnicwarehouse.inventoryNew.articles.domain.useCase

import com.example.catnicwarehouse.inventoryNew.articles.domain.repository.InventoryItemRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetInventoryItemsUseCase  @Inject constructor(
    private val inventoryItemRepository: InventoryItemRepository,
) {
    operator fun invoke(
        id:String,
    ): Flow<Resource<List<InventoryItem>>> = flow {
        emit(Resource.Loading())

        try {
            val response = inventoryItemRepository.getInventoryItems(id)
            if (response.isSuccessful){
                val inventoryItems = response.body()
                inventoryItems?.let { items ->
                    emit(Resource.Success(items))
                }
            }else{
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))

        }
    }
}