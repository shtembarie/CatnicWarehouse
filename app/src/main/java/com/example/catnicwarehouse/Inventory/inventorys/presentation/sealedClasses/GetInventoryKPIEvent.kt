package com.example.catnicwarehouse.Inventory.matchFoundStockYard.presentation.sealedClasses

sealed class GetInventoryKPIEvent{
    object LoadInventory: GetInventoryKPIEvent()
    object Reset : GetInventoryKPIEvent()
}
