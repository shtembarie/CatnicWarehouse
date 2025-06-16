package com.example.catnicwarehouse.inventoryNew.shared.viewModel

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventorySharedViewModel @Inject constructor() : BaseViewModel() {

    var selectedInventoryId: Int? = null
    var scannedStockyard: WarehouseStockyardsDTO? = null
    var scannedArticle: ArticlesForDeliveryResponseDTO? = null
    var selectedArticle :WarehouseStockyardInventoryEntriesResponseModel?=null
    var articlesListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null


    var selectedInventoryItem: InventoryItem? = null

    var filteredInventoryItems: List<InventoryItem>? = null

    var updatedUnitCode:String? = null
    var updatedAmount:Int?= null
    var conflictingInventoryitems:List<InventoryItem>? = null
}