package com.example.catnicwarehouse.scan.presentation.sealedClass.StockyardTree

sealed class StockyardTreeEvent {
    data class SearchWarehouseStockyards(val searchTerm: String?="",val warehouseCode:String?="",val isFromUserSearch:Boolean): StockyardTreeEvent()
    object Reset: StockyardTreeEvent()
}