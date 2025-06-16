package com.example.shared.networking.network.packing.model.packingList

data class PackingItemDifferences(
    val articleId: String,
    val baseArticleUnitCode: String,
    val differenceArticleAmount: Int,
    val replacementPackingListTotalArticleAmount: Int,
    val totalArticleAmount: Int
)