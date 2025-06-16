package com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses

sealed class InventoryEvent{
    object LoadInventory : InventoryEvent()
    object Reset : InventoryEvent()
}
