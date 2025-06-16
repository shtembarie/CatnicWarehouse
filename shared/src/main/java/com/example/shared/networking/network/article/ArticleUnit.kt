package com.example.shared.networking.network.article

data class ArticleUnit(
    val amountPerBase: Double,
    val baseAmountPerUnit: Double,
    val grossWeight: Double,
    val gtin: String,
    val isBaseCode: Boolean,
    val labelText: String?,
    val netWeight: Double,
    val unitCode: String
)