package com.example.catnicwarehouse.incoming.comment.presentation.sealedClass

import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundViewState

sealed class CommentViewState {
    object Reset : CommentViewState()
    object Empty : CommentViewState()
    object Loading : CommentViewState()
    data class DeliveryNoteSaved(val isNoteSaved: Boolean?) : CommentViewState()
    data class Error(val errorMessage: String?) : CommentViewState()
}