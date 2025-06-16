package com.example.shared.repository.correctingStock.model


/**
 * Created by Enoklit on 07.11.2024.
 */
data class WarehouseStockYardsList(
    val id : Int,
    val hierarchyLevel: Int,
    val name: String,
    val warehouseTemplateId: Int,
    val warehouseCode: String,
    val parentStockId: Int,
    val length: Int,
    val width: Int,
    val height: Int,
    val geoLocationX: Int,
    val geoLocationY: Int,
    val currentWeight: Float,
    val defaultPickAndDropZone: Boolean,
    val defaultPackingZone: Boolean
)

