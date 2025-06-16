package com.example.shared.repository.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Enoklit on 07.08.2024.
 */
@Parcelize
data class GetInventoryByIdUIModelCurrentInventory(
    val id: Int,
    val warehouseCode: String,
    val createdBy: String,
    val changedBy: String,
    val createdTimestamp: String,
    val changedTimestamp: String,
    val status: String,
    val startTime: String,
    val endTime: String?,
    val inventoryItems: List<InventoryItem>,
    val warehouseStockYards: List<CurrentInventoryWarehouseStockYardsModel>,
    val warehouseName: String,
    val necessaryBookings: List<NecessaryBooking>
):Parcelable
