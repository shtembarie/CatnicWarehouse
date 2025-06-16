package com.example.shared.repository.inventory.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class InventoryUIModel(
    val id:Int,
    val warehouseCode: String,
    val status: String,
    val createdTimestamp: String
)
