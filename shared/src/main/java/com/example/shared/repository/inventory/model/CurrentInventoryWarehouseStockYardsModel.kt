package com.example.shared.repository.inventory.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Enoklit on 07.08.2024.
 */
@Parcelize
data class CurrentInventoryWarehouseStockYardsModel(
    val id: Int,
    val name: String,
    val inventoried: Boolean = true
):Parcelable