package com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass

import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel

sealed class DeliveryDetailsViewState{
    object Reset : DeliveryDetailsViewState()
    object Empty : DeliveryDetailsViewState()
    object Loading : DeliveryDetailsViewState()
    data class Delivery(val delivery: GetDeliveryResponseModel? ) : DeliveryDetailsViewState()

    data class DeliveryCompleted(val isDeliveryCompleted: Boolean?) : DeliveryDetailsViewState()
    data class Error(val errorMessage: String?) : DeliveryDetailsViewState()
}
