package com.example.catnicwarehouse.incoming.comment.domain.useCase

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.catnicwarehouse.incoming.comment.domain.repository.CommentRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class SetDeliveryNoteUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(
        deliveryId: String,
        deliveryNoteRequestModel: DeliveryNoteRequestModel
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = commentRepository.setDeliveryNote(
                deliveryId = deliveryId,
                deliveryNoteRequestModel = deliveryNoteRequestModel
            )

            if (response.isSuccessful) {

                emit(Resource.Success(true))

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