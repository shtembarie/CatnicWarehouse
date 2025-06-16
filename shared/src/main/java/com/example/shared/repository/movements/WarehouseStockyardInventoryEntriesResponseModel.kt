package com.example.shared.repository.movements

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WarehouseStockyardInventoryEntriesResponseModel(
    val amount: Float?,
    val articleId: String?,
    val articleReserved: Boolean?,
    val defectiveArticles: Boolean?,
    val id: Int?,
    val isMoving: Boolean?,
    val stockYardId: Int?,
    val stockYardName: String?,
    val unitCode: String?,
    val warehouseCode: String?,
    val articleMatchCode: String?,
    val articleDescription: String?,
    val reason: String?,
    var comment: String?,
    val isConnectedArticle:Boolean?,
    var amountTakenForPickUp: Float?
):Parcelable
