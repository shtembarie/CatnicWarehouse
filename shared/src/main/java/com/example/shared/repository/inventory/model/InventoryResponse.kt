package com.example.shared.repository.inventory.model

data class InventoryResponse(
    val id: Int,
    val warehouseCode: String,
    val status: String,
    val createdTimestamp:String
)



