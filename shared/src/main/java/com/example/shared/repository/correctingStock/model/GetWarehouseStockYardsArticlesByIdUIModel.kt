package com.example.shared.repository.correctingStock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Enoklit on 13.11.2024.
 */
@Parcelize
class GetWarehouseStockYardsArticlesByIdUIModel (
    val id: Int,
    val stockYardId: Int,
    val stockYardName: String,
    val articleId: String,
    val articleMatchCode: String,
    val articleDescription: String,
    val amount: Float,
    val unit: String,
    val defectiveArticles: Boolean,
    val isMoving: Boolean
): Parcelable