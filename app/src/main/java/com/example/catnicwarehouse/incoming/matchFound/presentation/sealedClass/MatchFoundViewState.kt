package com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass

sealed class MatchFoundViewState{
    object Reset : MatchFoundViewState()
    object Empty : MatchFoundViewState()
    object Loading : MatchFoundViewState()
    data class DeliveryItemCreated(val deliveryId: String,val deliveryItemId: Int) : MatchFoundViewState()

    data class DeliveryItemUpdated(val isItemUpdated: Boolean?) : MatchFoundViewState()
    data class DeliveryItemBooked(val isItemCreated: Boolean?) : MatchFoundViewState()
    data class Error(val errorMessage: String?) : MatchFoundViewState()
}
