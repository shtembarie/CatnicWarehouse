package com.example.catnicwarehouse.incoming.deliveries.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.deliveries.domain.model.DeliveryUIModel
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel


sealed class DeliveryViewState {
    object Reset : DeliveryViewState()
    object Empty : DeliveryViewState()
    object Loading : DeliveryViewState()
    data class Deliveries(val deliveries: List<DeliveryUIModel>? ) : DeliveryViewState()
    data class Delivery(val delivery: GetDeliveryResponseModel? ) : DeliveryViewState()
    data class Error(val errorMessage: String?) : DeliveryViewState()
}
