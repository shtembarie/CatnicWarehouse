package com.example.catnicwarehouse.scan.presentation.helper

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

fun WarehouseStockyardsDTO.getFullPath(): String {
    val pathSegments = mutableListOf<String>()
    var current: WarehouseStockyardsDTO? = this
    while (current != null) {
        pathSegments.add(current.name)
        current = current.parent // Assume you have a reference to the parent item
    }
    return pathSegments.asReversed().joinToString(" > ")
}
