package com.example.catnicwarehouse.incoming.comment.domain.repository

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import retrofit2.Response

interface CommentRepository {
    suspend fun setDeliveryNote(
        deliveryId: String,
        deliveryNoteRequestModel: DeliveryNoteRequestModel
    ): Response<Unit>
}