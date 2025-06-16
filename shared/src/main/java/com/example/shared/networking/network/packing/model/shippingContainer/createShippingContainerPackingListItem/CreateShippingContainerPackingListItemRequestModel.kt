package com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem

data class CreateShippingContainerPackingListItemRequestModel(
    val packingListId: String?,
    val items: List<Item>?,
    val height: Float?,
    val width: Float?,
    val netWeight: Float?,
    val grossWeight: Float?,
    val reference: String?,
    val code: Int?,
    val depth: Float?,
    val description: String?,
    val shippingContainerId:String? = null,
    val sscc:String? = null
)