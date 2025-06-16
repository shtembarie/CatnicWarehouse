package com.example.shared.repository.movements

data class WarehouseStockyardInventoryResponseModel(
    val id: Int?,
    val stockYardId:Int?,
    val articleId: String?,
    val articleMatchCode: String?,
    val articleDescription: String?,
    val amount: Float?,
    val unit: String?,
    val defectiveArticles: Boolean?,
    val isMoving: Boolean?,
    //Internal field
    var quantityTakenForDropOff:Float?,
    var quantityTakenForPickUp:Float?
)
