package com.example.shared.repository.movements

data class MovementItem(
    var articleMatchCode: String?,
    var amount: Int,
    var articleId: String,
    var destinationWarehouseStockYardId: Int,
    var destinationWarehouseStockYardInventoryEntryId: Int,
    var id: Int,
    var movementId: Int,
    var movementOpen: Boolean,
    var sourceWarehouseStockYardId: Int,
    var sourceWarehouseStockYardInventoryEntryId: Int,
    var unitCode: String,
    var warehouseCode: String,
    var sourceWarehouseStockYardName: String?,
    var destinationWarehouseStockYardName: String?,
    //Internal field
    var amountTakenForDropOff: Float?,
)