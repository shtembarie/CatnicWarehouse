package com.example.shared.repository.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NecessaryBooking(
    val warehouseStockYardId: Int,
    val warehouseStockYardName: String,
    val warehouseStockYardInventoryEntryId: Int,
    val inventoryItemId: Int,
    val articleId: String,
    val articleDescription: String,
    val targetStock: Float,
    val targetUnitCode: String,
    val actualStock: Int,
    val actualUnitCode: String,
    val newWarehouseStockYardInventoryEntry: Boolean,
    val differenceInBaseUnit: Float,
    val baseUnitCode: String
):Parcelable
