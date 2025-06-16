package com.example.shared.repository.movements

data class DropOffRequestModel(
    var movementItemId: Int?,
    var destinationWarehouseStockYardId: Int?,
    var unitCode: String?,
    var amount: Int?
)
