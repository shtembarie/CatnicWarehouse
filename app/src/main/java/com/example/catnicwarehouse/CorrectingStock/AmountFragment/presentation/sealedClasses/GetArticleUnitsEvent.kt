package com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses

/**
 * Created by Enoklit on 19.11.2024.
 */
sealed class GetArticleUnitsEvent {
    data class Loading(val articleId: String) : GetArticleUnitsEvent()
    object Reset : GetArticleUnitsEvent()
}
