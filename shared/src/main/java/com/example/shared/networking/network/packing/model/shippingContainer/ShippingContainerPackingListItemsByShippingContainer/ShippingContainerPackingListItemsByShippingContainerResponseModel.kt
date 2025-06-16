package com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer

data class ShippingContainerPackingListItemsByShippingContainerResponseModel(
    val code: Int?,
    val depth: Float?,
    val grossWeight: Float?,
    val height: Float?,
    val items: List<Item>?,
    val netWeight: Float?,
    val own_weight: Float?,
    val packingListId: String?,
    val reference: String?,
    val shippingContainerId: String?,
    val sscc: String?,
    val width: Float?
)