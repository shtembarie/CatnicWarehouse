package com.example.catnicwarehouse.movement.shared

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.movements.GetMovementsModelItem
import com.example.shared.repository.movements.MovementItem
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MovementsSharedViewModel @Inject constructor() : BaseViewModel() {

    var warehouseCode = IncomingConstants.WarehouseParam
    var movementItemsList :ArrayList<MovementItem>? = null
    var currentMovementItemToDropOff :MovementItem? = null
    var movementActionType:MovementActionType? = null
    var scannedStockyard:WarehouseStockyardsDTO? = null
    var scannedArticle:ArticlesForDeliveryResponseDTO? = null
    var scannedArticleList:List<ArticlesForDeliveryResponseDTO>? = null

    var articlesList:ArrayList<WarehouseStockyardInventoryResponseModel>? = null
    var currentMovement: GetMovementsModelItem? = null
    var selectedWarehouseStockyardInventoryEntry: WarehouseStockyardInventoryEntriesResponseModel? = null
    var selectedArticle :WarehouseStockyardInventoryEntriesResponseModel?=null
    var articlesListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null
    var movementItemsListToSelectFrom :List<MovementItem>?=null
    var itemsDropped:Int = 0


    fun initViewModel() {
        movementItemsList= null
        scannedStockyard = null
        articlesList = null
        currentMovement = null
        scannedArticle= null
        movementActionType = null
        selectedWarehouseStockyardInventoryEntry = null
        currentMovementItemToDropOff = null
        articlesListToSelectFrom = null
        movementItemsListToSelectFrom = null
        itemsDropped = 0
        scannedArticleList= null
    }


    fun reset() {
        scannedStockyard = null
        scannedArticle= null
        scannedArticleList = null
        scannedStockyard = null
        movementActionType = null
        selectedWarehouseStockyardInventoryEntry = null
        articlesListToSelectFrom = null
        currentMovementItemToDropOff = null
        movementItemsListToSelectFrom = null
    }


}