package com.example.shared.networking.network.purchaseOrder.model

data class CreateDeliveryItemRequestModel(
    var articleId: String?,
    var unitCode: String?,
    var amount: Float=1.0f,
    var defectiveAmount: Int=0,
    var defectiveUnitCode: String?,
    var reason: String?,
    var comment: String?,
    var warehouseStockYardId: Int=0
)
