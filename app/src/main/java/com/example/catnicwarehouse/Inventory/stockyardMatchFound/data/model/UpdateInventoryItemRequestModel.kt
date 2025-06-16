package com.example.catnicwarehouse.Inventory.stockyardMatchFound.data.model

data class UpdateInventoryItemRequestModel(
    val actualUnitCode: String?,
    val actualStock: Int=1,
    val comment: String,
)
