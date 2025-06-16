package com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses

import com.example.shared.repository.inventory.model.InventorizeItemRequestModel

sealed class MatchFoundEvent {
    object Reset : MatchFoundEvent()
    data class InventorizeItem(
        val id: Int?,
        val inventorizeItemRequestModel: InventorizeItemRequestModel?
    ) : MatchFoundEvent()

}