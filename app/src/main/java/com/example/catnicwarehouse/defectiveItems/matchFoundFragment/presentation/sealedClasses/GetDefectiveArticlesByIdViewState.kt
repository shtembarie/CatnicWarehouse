package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses

import com.example.shared.repository.defectiveArticles.GetDefectiveArticleByIdUIModel

/**
 * Created by Enoklit on 05.12.2024.
 */
sealed class GetDefectiveArticlesByIdViewState{
    object Reset: GetDefectiveArticlesByIdViewState()
    object Empty: GetDefectiveArticlesByIdViewState()
    object Loading: GetDefectiveArticlesByIdViewState()

    data class Error(val errorMessage: String?) : GetDefectiveArticlesByIdViewState()
    data class DefectiveArticlesById(val id: GetDefectiveArticleByIdUIModel) : GetDefectiveArticlesByIdViewState()
    data class CreateNewDefectiveItem(val isSuccess: Boolean?) : GetDefectiveArticlesByIdViewState()

}
