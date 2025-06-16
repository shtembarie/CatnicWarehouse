package com.example.catnicwarehouse.inventoryNew.comment.domain.useCase

import com.example.catnicwarehouse.inventoryNew.comment.domain.repository.InventoryCommentRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateInventoryCommentUseCase @Inject constructor(
    private val inventoryCommentRepository: InventoryCommentRepository
) {
    operator fun invoke(
        id: String?,
        itemId: String?,
        deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val response = inventoryCommentRepository.updateInventoryComment(
                id,
                itemId,
                deliveryNoteRequestModel
            )
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
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