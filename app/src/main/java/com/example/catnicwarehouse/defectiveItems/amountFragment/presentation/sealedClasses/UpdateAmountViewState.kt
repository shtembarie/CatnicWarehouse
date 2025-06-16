package com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses

import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState

/**
 * Created by Enoklit on 05.12.2024.
 */
sealed class UpdateAmountViewState{
    object Reset : UpdateAmountViewState()
    object Empty : UpdateAmountViewState()
    object Loading : UpdateAmountViewState()

    data class Error(val errorMessage: String?) : UpdateAmountViewState()
    data class UpdatedAmountResult(val isAmountUpdatet: Boolean?) : UpdateAmountViewState()



}
