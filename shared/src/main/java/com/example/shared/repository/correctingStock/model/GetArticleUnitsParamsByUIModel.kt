package com.example.shared.repository.correctingStock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Enoklit on 19.11.2024.
 */
@Parcelize
class GetArticleUnitsParamsByUIModel (
    val unitCode: String,
    val gtin: String,
    val labelText: String? = null,
    val netWeight: Float? = null,
    val grossWeight: Float,
    val baseAmountPerUnit: Float,
    val amountPerBase: Float,
    val isBaseCode: Boolean
): Parcelable