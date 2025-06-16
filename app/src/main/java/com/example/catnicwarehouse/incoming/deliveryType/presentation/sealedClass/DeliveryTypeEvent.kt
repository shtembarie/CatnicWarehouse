package com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass

import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel

sealed class DeliveryTypeEvent {
    data class CreateDelivery(val createDeliveryRequestModel: CreateDeliveryRequestModel): DeliveryTypeEvent()
    object Reset: DeliveryTypeEvent()
}