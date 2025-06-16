package com.example.catnicwarehouse.packing.shared.presentation.viewModel

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.packing.model.packingItem.PackingItem
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.networking.network.packing.model.packingList.WarehouseStockYardPicking
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import javax.inject.Inject

class PackingSharedViewModel @Inject constructor() : BaseViewModel() {

    var warehouseCode = IncomingConstants.WarehouseParam
    var selectedAssignedPackingListItem: AssignedPackingListItem? = null
    var selectedAssignedPackingListGroupItem: AssignedPackingListItem? = null
    var selectedPackingListItem:PackingModelItem? = null
    var selectedSearchedPackingListId : String? = null
    var selectedRelatedPackingListItem:PackingModelItem? = null
    var packingItems: List<PackingItem>? = null
    var selectedPackingItemToPack: PackingItem? = null
    var itemsForPacking:List<GetItemsForPackingResponseModelItem>? = null
    var selectedItemForPacking: WarehouseStockYardPicking? = null
    var scannedArticle:ArticlesForDeliveryResponseDTO?=null
    var scannedArticleList:List<ArticlesForDeliveryResponseDTO>? = null
    var scannedStockyard:WarehouseStockyardsDTO? = null
    var selectedArticle :WarehouseStockyardInventoryEntriesResponseModel?=null
    var articlesListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null
    var stockyardsListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null
    var selectedStockyardIdInventoryEntry :WarehouseStockyardInventoryEntriesResponseModel?=null
    var dropzoneScannedStockyardId:Int? = null

    fun initViewModel() {
        selectedAssignedPackingListItem = null
        packingItems = null
        selectedPackingItemToPack = null
        scannedArticle = null
        scannedStockyard = null
        selectedArticle = null
        articlesListToSelectFrom = null
        stockyardsListToSelectFrom = null
        selectedStockyardIdInventoryEntry= null
        dropzoneScannedStockyardId = null
        selectedPackingListItem= null
        itemsForPacking = null
        selectedItemForPacking = null
        selectedSearchedPackingListId = null
        selectedAssignedPackingListGroupItem = null
        scannedArticleList = null
    }


    fun reset() {

    }


}