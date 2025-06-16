package com.example.catnicwarehouse.checks.shared.presentation.sealedClasses

sealed class ChecksSharedViewState {
    object Reset : ChecksSharedViewState()
    object Empty : ChecksSharedViewState()
    object Loading : ChecksSharedViewState()
    data class Error(val errorMessage: String?) : ChecksSharedViewState()
}