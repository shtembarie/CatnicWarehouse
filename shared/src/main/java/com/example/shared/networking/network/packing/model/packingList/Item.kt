package com.example.shared.networking.network.packing.model.packingList

data class Item(
    val amount: Int?,
    val articleId: String?,
    val description: String?,
    val gtin: String?,
    val iposition: Int?,
    val lgid: String?,
    val orderItemLgid: String?,
    val orderItemOrderId: String?,
    val orderItemPosition: String?,
    val packedAmount: Int?,
    val packedStatus: String?,
    val packingListId: String?,
    val position: String?,
    val typeCode: String?,
    val unitCode: String?
)