package com.example.catnicwarehouse.incoming.comment.presentation.sealedClass

import com.example.shared.networking.network.delivery.model.setDeliveryNote.DeliveryNoteRequestModel

sealed class CommentEvent{
    data class SaveDeliveryNote(val note:String,val noteRequestModel: DeliveryNoteRequestModel): CommentEvent()
}
