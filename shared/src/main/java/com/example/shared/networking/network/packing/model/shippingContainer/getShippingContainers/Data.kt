package com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val Depth: Int,
    val Height: Int,
    val Id: String,
    val PackingList_id: String,
    val PackingList_position: Int,
    val Sscc: String,
    val Type_Description: String,
    val Weight: Double,
    val Width: Int
) :Parcelable