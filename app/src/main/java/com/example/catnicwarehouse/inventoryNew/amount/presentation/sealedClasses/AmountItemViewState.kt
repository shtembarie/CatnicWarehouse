package com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses

import com.example.shared.networking.network.article.ArticleUnit

sealed class AmountItemViewState{
    object Reset : AmountItemViewState()
    object Empty : AmountItemViewState()
    object Loading : AmountItemViewState()
    data class Error(val errorMessage: String?) : AmountItemViewState()

    data class ArticleUnits(val articleUnits: List<ArticleUnit>?) : AmountItemViewState()
}
