package com.example.shared.networking.network.Stockyards

data class WarehouseStockyardsDTO(
    val id: Int,
    val name: String,
    val warehouseTemplateId: Int,
    val warehouseCode: String,
    val parentStockId: Int?,
    val length: Double,
    val width: Double,
    val height: Double,
    val maxWeight: Double,
    val geoLocationX: Double,
    val geoLocationY: Double,
    val currentWeight: Double,
    val defaultPickAndDropZone: Boolean,
    val hierarchyLevel: Int,
    var children: MutableList<WarehouseStockyardsDTO> = mutableListOf(),
    var isExpanded: Boolean = false,
    var parent: WarehouseStockyardsDTO? = null
){
override fun toString(): String {
    // Avoid printing children and parent in a way that could cause recursion
    return "WarehouseStockyardsDTO(id=$id, hierarchyLevel=$hierarchyLevel, name=$name, parentStockId=$parentStockId)"
}
}
