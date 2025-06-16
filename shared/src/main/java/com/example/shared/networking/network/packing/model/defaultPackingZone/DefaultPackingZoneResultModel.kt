package com.example.shared.networking.network.packing.model.defaultPackingZone

data class DefaultPackingZoneResultModel(
    val currentWeight: Float?,
    val defaultPackingZone: Boolean?,
    val defaultPickAndDropZone: Boolean?,
    val geoLocationX: Int?,
    val geoLocationY: Int?,
    val height: Int?,
    val hierarchyLevel: Int?,
    val id: Int?,
    val length: Int?,
    val maxWeight: Long?,
    val name: String?,
    val parentStockId: Int?,
    val warehouseCode: String?,
    val warehouseTemplateId: Int?,
    val width: Int?
)