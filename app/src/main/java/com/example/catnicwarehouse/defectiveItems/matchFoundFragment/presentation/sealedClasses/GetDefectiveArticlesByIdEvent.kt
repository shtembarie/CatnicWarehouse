package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses

import com.example.catnicwarehouse.movement.summary.presentation.sealedClasses.MovementSummaryEvent
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import com.example.shared.repository.movements.DropOffRequestModel

/**
 * Created by Enoklit on 05.12.2024.
 */
sealed class GetDefectiveArticlesByIdEvent {
    data class Loading(val id: Int): GetDefectiveArticlesByIdEvent()
    object Reset: GetDefectiveArticlesByIdEvent()
    data class PostArticles(val postDefectiveArticlesModel: PostDefectiveArticlesModel?): GetDefectiveArticlesByIdEvent()
}