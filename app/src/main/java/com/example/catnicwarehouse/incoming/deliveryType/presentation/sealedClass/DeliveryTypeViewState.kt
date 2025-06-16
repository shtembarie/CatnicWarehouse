package com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass

sealed class DeliveryTypeViewState{
    object Reset : DeliveryTypeViewState()
    object Empty : DeliveryTypeViewState()
    object Loading : DeliveryTypeViewState()
    data class DeliveryCreated(val createdDelivery: String?) : DeliveryTypeViewState()
    data class Error(val errorMessage: String?) : DeliveryTypeViewState()
}
