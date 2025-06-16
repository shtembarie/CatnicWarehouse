package com.example.catnicwarehouse.inventoryNew.matchFound.domain.useCase

import com.example.catnicwarehouse.inventoryNew.comment.domain.repository.InventoryCommentRepository
import com.example.catnicwarehouse.inventoryNew.matchFound.domain.repository.InventoryMatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.repository.inventory.model.InventoriseItemResponseModel
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventorizeItemUseCase @Inject constructor(
    private val inventoryMatchFoundRepository: InventoryMatchFoundRepository
) {
    operator fun invoke(
        id: String?,
        inventorizeItemRequestModel: InventorizeItemRequestModel?
    ): Flow<Resource<InventoriseItemResponseModel?>> = flow {
        emit(Resource.Loading())

        try {
            val response = inventoryMatchFoundRepository.inventorizeItem(
                id,
                inventorizeItemRequestModel
            )
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
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