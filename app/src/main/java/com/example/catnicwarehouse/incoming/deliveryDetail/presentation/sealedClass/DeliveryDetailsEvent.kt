package com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass

import com.example.catnicwarehouse.incoming.articles.presentation.sealedClass.ArticlesEvent

sealed class DeliveryDetailsEvent{
    data class GetDelivery(var id: String) : DeliveryDetailsEvent()
    data class CompleteDelivery(var id: String) : DeliveryDetailsEvent()
    object Reset : DeliveryDetailsEvent()
}
