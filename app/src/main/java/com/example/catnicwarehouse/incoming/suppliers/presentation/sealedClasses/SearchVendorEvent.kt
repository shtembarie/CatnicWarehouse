package com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel

sealed class SearchVendorEvent {
    data class SearchVendor(val searchTerm:String?):SearchVendorEvent()
    data class CreateDelivery(val createDeliveryRequestModel: CreateDeliveryRequestModel): SearchVendorEvent()
    data class SearchArticle(val searchTerm: String): SearchVendorEvent()
    object EmptySearch:SearchVendorEvent()
}