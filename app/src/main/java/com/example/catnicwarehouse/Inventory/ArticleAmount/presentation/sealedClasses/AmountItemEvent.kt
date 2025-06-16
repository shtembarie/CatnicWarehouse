package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.sealedClasses

import com.example.shared.repository.inventory.model.SetInventoryItems

sealed class AmountItemEvent {
    data class UpdateInventoryItem(val stockyardId:Int?, val itemId:Int?, val setInventoryItems: SetInventoryItems?) : AmountItemEvent()
    object Empty : AmountItemEvent()
}