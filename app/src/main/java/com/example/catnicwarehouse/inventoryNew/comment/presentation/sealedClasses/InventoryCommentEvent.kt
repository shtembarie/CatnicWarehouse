package com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel

sealed class InventoryCommentEvent {
    object Reset : InventoryCommentEvent()
    data class UpdateComment(
        val id: String?,
        val itemId: String?,
        val deliveryNoteRequestModel: DeliveryNoteRequestModel?
    ) : InventoryCommentEvent()

}