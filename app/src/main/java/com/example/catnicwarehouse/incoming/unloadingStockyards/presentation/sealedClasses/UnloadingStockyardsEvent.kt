package com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeEvent
import com.example.catnicwarehouse.scan.presentation.sealedClass.manualInput.ManualInputEvent
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.networking.network.warehouse.FindWarehouseDTO

sealed class UnloadingStockyardsEvent {
    data class FindWarehouses(val searchTerm: String?) : UnloadingStockyardsEvent()
    data class FindDefaultPickUpAndDropZones(val warehouseCode: String) : UnloadingStockyardsEvent()
    data class SearchArticle(val searchTerm: String): UnloadingStockyardsEvent()
    data class GetWarehouseStockyardById(val id: String): UnloadingStockyardsEvent()
    object Reset : UnloadingStockyardsEvent()
}