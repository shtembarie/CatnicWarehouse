package com.example.catnicwarehouse.movement.summary.presentation.sealedClasses

sealed class MovementSummaryViewState{
    object Reset : MovementSummaryViewState()
    object Empty : MovementSummaryViewState()
    object Loading : MovementSummaryViewState()
    data class Error(val errorMessage: String?) : MovementSummaryViewState()
    data class DropOffResult(val isSuccess: Boolean?) : MovementSummaryViewState()

}