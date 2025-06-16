package com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShippingContainerTypeResponseModelItem(
    var code: Int?,
    var depth: Float?,
    var description: String?,
    var height: Float?,
    var own_weight: Float?,
    var width: Float?,
) : Parcelable