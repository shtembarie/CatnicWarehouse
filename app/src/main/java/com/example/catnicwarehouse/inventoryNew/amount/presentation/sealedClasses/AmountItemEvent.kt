package com.example.catnicwarehouse.inventoryNew.amount.presentation.sealedClasses

sealed class AmountItemEvent{
    data class GetArticleUnits(val articleId :String):AmountItemEvent()
    object Reset: AmountItemEvent()
}
