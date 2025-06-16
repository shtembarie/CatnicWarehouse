package com.example.shared.networking.network.article

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ArticlesForDeliveryResponseDTO(
    val articleId: String,
    val matchCode: String?,
    val description: String?,
    val quantityInPurchaseOrders: Float?,
    //val quantityInPurchaseOrders: Double,
    val unitCode: String?,
    //Internal field
    var quantityTakenForDropOff:Float?,
    var quantityTakenForPickUp:Float?
):Parcelable


