package com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass

import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel

sealed class MatchFoundEvent {
    data class CreateDeliveryItem(
        val deliveryId: String,
        val createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ) : MatchFoundEvent()

    data class BookDeliveryItem(
        val deliveryId: String,
        val deliveryItemId: String
    ) : MatchFoundEvent()

    data class UpdateDeliveryItem(
        val deliveryId: String,
        val deliveryItemId: String,
        val updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ) : MatchFoundEvent()

    object Reset:MatchFoundEvent()
}