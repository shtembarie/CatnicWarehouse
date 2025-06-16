package com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeEvent
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel

sealed class SearchCustomerEvent {
    data class SearchCustomer(val searchTerm:String?):SearchCustomerEvent()
    data class CreateDelivery(val createDeliveryRequestModel: CreateDeliveryRequestModel): SearchCustomerEvent()
    data class SearchArticle(val searchTerm: String): SearchCustomerEvent()
    object EmptySearch:SearchCustomerEvent()
}