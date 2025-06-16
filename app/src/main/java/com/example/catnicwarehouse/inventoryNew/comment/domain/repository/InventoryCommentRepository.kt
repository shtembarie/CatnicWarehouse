package com.example.catnicwarehouse.inventoryNew.comment.domain.repository

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel
import com.example.shared.repository.inventory.model.InventoryItem
import retrofit2.Response

interface InventoryCommentRepository {
    suspend fun updateInventoryComment(
        id: String?,
        itemId: String?,
        deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ): Response<Unit>
}