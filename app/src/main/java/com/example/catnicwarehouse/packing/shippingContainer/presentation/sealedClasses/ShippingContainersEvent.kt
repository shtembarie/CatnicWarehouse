package com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses

import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel

sealed class ShippingContainersEvent {
    data class GetShippingContainers(val packingListId: String?) : ShippingContainersEvent()
    object GetShippingContainerTypes : ShippingContainersEvent()
    data class CreateShippingContainerPackingListItem(val packingListId: String?,val createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel) : ShippingContainersEvent()
    data class UpdateShippingContainerPackingListItem(val packingListId: String?,val createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel) : ShippingContainersEvent()
    data class GetShippingContainerPackingListItemsByShippingContainer(val packingListId: String?,val shippingContainerId:String?) : ShippingContainersEvent()
    object Empty : ShippingContainersEvent()
}