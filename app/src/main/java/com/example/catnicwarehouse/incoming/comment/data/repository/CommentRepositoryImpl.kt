package com.example.catnicwarehouse.incoming.comment.data.repository

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.catnicwarehouse.incoming.comment.domain.repository.CommentRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : CommentRepository {
    override suspend fun setDeliveryNote(
        deliveryId: String,
        deliveryNoteRequestModel: DeliveryNoteRequestModel
    ): Response<Unit> {
        return warehouseApiServices.setDeliveryNote(
            deliveryId = deliveryId,
            deliveryNoteRequestModel = deliveryNoteRequestModel
        )
    }

}
