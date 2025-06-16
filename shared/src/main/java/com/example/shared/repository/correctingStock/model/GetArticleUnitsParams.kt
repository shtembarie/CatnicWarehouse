package com.example.shared.repository.correctingStock.model

/**
 * Created by Enoklit on 19.11.2024.
 */
data class GetArticleUnitsParams(
    val unitCode: String,
    val gtin: String,
    val labelText: String? = null,
    val netWeight: Float? = null,
    val grossWeight: Float,
    val baseAmountPerUnit: Float,
    val amountPerBase: Float,
    val isBaseCode: Boolean
)
