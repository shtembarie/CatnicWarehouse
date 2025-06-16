package com.example.shared.networking.network.packing.model.amount

data class PickAmountRequestModel(
    val packedAmount: Int?,
    val unitCode: String?,
    val warehouseStockYardInventoryId: Int?
)