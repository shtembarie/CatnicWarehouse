package com.example.catnicwarehouse.Inventory.AddArticle.domain.useCases

import com.example.catnicwarehouse.Inventory.AddArticle.domain.repository.InventoryAddArticleRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddInventoryArticleUseCase @Inject constructor(
    private val inventoryAddArticleRepository: InventoryAddArticleRepository
) {
    operator fun invoke(
        id: Int,
        stockyardId: Int,
        command: PostStockyardItemCommand
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            val response = inventoryAddArticleRepository.findInventoryItems(id = id, stockyardId = stockyardId, command)
            if(response.isSuccessful){
                val inventoryItemArticle = response.body()
                inventoryItemArticle?.let { inventories ->
                    emit(Resource.Success(inventories))
                }!!
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
