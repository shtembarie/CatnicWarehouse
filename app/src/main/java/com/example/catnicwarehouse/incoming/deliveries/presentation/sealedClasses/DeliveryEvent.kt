package com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses

sealed class DeliveryEvent {
    object LoadDelivery : DeliveryEvent()
    data class GetDelivery(var id: String) : DeliveryEvent()
    object Reset : DeliveryEvent()
}
