package com.example.shared.networking.network.packing.model.packingList

data class GetItemsForPackingResponseModelItem(
    val articleId: String,
    val id: String,
    val itemAmount: Int,
    val packedAmount: Int,
    val unitCode: String,
    val warehouseStockYardPickings: List<WarehouseStockYardPicking>
)