package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses

import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.incoming.comment.presentation.sealedClass.CommentViewState

/**
 * Created by Enoklit on 18.11.2024.
 */
sealed class CorrectInventorViewState {
    object Reset : CorrectInventorViewState()
    object Empty : CorrectInventorViewState()
    object Loading : CorrectInventorViewState()

    data class CorrectedInventorySaved(val isCorrected: Boolean?) : CorrectInventorViewState()
    data class Error(val errorMessage: String?) : CorrectInventorViewState()

}
