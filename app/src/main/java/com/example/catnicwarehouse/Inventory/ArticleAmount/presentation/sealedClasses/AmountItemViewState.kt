package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses

sealed class AmountItemViewState {
    object Reset : AmountItemViewState()
    object Empty : AmountItemViewState()
    object Loading : AmountItemViewState()
    data class Error(val errorMessage: String?) : AmountItemViewState()
    data class InventoryItemUpdated(val isItemUpdated: Boolean?) : AmountItemViewState()
}