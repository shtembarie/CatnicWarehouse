package com.example.shared.repository.defectiveArticles

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Enoklit on 04.12.2024.
 */
@Parcelize
data class GetDefectiveArticleUIModel(
    val id: Int?,
    val warehouseStockYardInventoryEntryId: Int?,
    val warehouseCode: String?,
    val warehouseName: String?,
    val warehouseStockYardId: Int?,
    val articleId: String?,
    val articleMatchCode: String?,
    val articleDescription: String?,
    val unitCode: String?,
    val defectiveAmount: Int?,
    val restoredAmount: Int?,
    val originType: String?,
    val originObjectId: String?,
    val reportedTimestamp: String?,
    val reportedBy: String?,
    val changedTimestamp: String?,
    val changedBy: String?,
    var reason: String?,
    val comment: String?,
    val status: String?
):Parcelable
