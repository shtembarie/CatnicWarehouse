package com.example.catnicwarehouse.Inventory.matchFoundStockYard.data.model

data class GetInventoryKPIUIModel(
    val id: Int,
    val totalItems: Int,
    val totalInventoriedItems: Int,
    val inventoryCountAccuracyPercent: Float,
    val inventoryItemCountWithDiscrepancy: Int,
    val inventoryCountDiscrepancyPercent: Float,
    val inventoryItemCountWithAccuracy: Int,
    val inventoryDiscrepancyStandardPriceValue: Float,
    val durationHours: Float

)
