package com.example.shared.repository.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InventoryItem(
    val id: Int,
    val warehouseStockYardId: Int,
    val changedTimestamp: String,
    val changedBy: String,
    val actualStock: Int,
    val warehouseCode : String,
    val warehouseStockYardName: String,
    val warehouseStockYardInventoryId: Int,
    val articleId: String,
    val articleDescription: String,
    val articleMatchcode: String,
    val targetStock: Float,
    val targetUnitCode: String?,
    val actualUnitCode: String?,
    val inventoried: Boolean,
    var comment: String,
    val differenceInBaseUnit: Float,
    val baseUnitCode: String?,
    val newWarehouseStockYardInventory: Boolean
) :Parcelable













