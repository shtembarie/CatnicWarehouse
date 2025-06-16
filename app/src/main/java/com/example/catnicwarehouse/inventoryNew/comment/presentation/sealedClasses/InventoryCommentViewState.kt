package com.example.catnicwarehouse.inventoryNew.comment.presentation.sealedClasses

sealed class InventoryCommentViewState {
    object Empty : InventoryCommentViewState()
    object Loading : InventoryCommentViewState()
    data class Error(val errorMessage: String?) : InventoryCommentViewState()
    data class CommentUpdated(val comment:String?) : InventoryCommentViewState()

}