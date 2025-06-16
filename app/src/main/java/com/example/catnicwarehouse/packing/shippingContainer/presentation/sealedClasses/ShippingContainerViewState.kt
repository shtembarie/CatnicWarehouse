package com.example.catnicwarehouse.packing.shippingContainer.presentation.sealedClasses

import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.GetShippingContainersResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem

sealed class ShippingContainerViewState {
    object Reset : ShippingContainerViewState()
    object Empty : ShippingContainerViewState()
    object Loading : ShippingContainerViewState()
    data class Error(val errorMessage: String?) : ShippingContainerViewState()
    data class GetShippingContainersResult(val shippingContainers: GetShippingContainersResponseModel?) : ShippingContainerViewState()
    data class GetShippingContainerTypesResult(val shippingContainerTypes: List<ShippingContainerTypeResponseModelItem>?) : ShippingContainerViewState()
    data class GetShippingContainerPackingListItemsByShippingContainerResult(val shippingContainerTypes: ShippingContainerPackingListItemsByShippingContainerResponseModel?) : ShippingContainerViewState()
    data class CreateShippingContainerPackingListItemResult(val shippingContainerPackingListItemCreated:Boolean?) : ShippingContainerViewState()
    data class UpdateShippingContainerPackingListItemResult(val shippingContainerPackingListItemUpdated:Boolean?) : ShippingContainerViewState()
}