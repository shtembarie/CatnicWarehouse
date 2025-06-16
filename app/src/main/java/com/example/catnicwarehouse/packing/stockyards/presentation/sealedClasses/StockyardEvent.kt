package com.example.catnicwarehouse.packing.stockyards.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsEvent

sealed class StockyardEvent {
    data class GetWarehouseStockyardsByWarehouseCode(val warehouseCode: String?) :
        StockyardEvent()
    data class GetWarehouseStockyardById(val id: String): StockyardEvent()
    object Reset: StockyardEvent()
}
