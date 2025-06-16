package com.example.catnicwarehouse.CorrectingStock.AmountFragment.presentation.sealedClasses

import com.example.shared.repository.correctingStock.model.GetArticleUnitsParams

/**
 * Created by Enoklit on 19.11.2024.
 */
sealed class GetArticleUnitsViewState {
    object Reset : GetArticleUnitsViewState()
    object Empty : GetArticleUnitsViewState()
    object Loading : GetArticleUnitsViewState()
    data class Error(val errorMessage: String?) : GetArticleUnitsViewState()

    data class ArticleUnitId(val articleUnits: List<GetArticleUnitsParams>?) : GetArticleUnitsViewState()
}
