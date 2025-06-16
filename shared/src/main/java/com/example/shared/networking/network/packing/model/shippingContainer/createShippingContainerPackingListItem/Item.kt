package com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem

data class Item(
    val description: String?,
    val iposition: Int?,
    val lgid: String?,
    val orderItemPosition: String?,
    val packedAmount: String?,
    val packedAmountItem:String?,
    val packingListId: String?,
    val position: String?,
    val typeCode: String?,
    val unitCode: String?
)