package com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses

import com.example.shared.repository.inventory.model.SetInventoryItems

sealed class MatchFoundInventoryItemEvent{

    data class UpdateInventoryItems(
        val id : Int,
        val inventoryItemsList: ArrayList<Pair<Int, SetInventoryItems>>
    ) : MatchFoundInventoryItemEvent()

    object Reset: MatchFoundInventoryItemEvent()
    object LoadInventory: MatchFoundInventoryItemEvent()
}
