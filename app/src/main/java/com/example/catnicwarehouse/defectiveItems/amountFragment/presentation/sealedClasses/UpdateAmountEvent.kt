package com.example.catnicwarehouse.defectiveItems.amountFragment.presentation.sealedClasses

import com.example.shared.repository.defectiveArticles.SetAmount

/**
 * Created by Enoklit on 05.12.2024.
 */
sealed class UpdateAmountEvent{
    object Empty : UpdateAmountEvent()
    data class UpdatetAmount(val id : Int?, val setAmount: SetAmount): UpdateAmountEvent()
}
