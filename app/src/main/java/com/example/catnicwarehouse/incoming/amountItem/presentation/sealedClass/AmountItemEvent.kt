package com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass

sealed class AmountItemEvent{
    data class GetArticleUnits(val articleId :String):AmountItemEvent()
    object Reset: AmountItemEvent()
}
