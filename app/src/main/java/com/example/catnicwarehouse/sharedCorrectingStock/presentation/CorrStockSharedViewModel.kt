package com.example.catnicwarehouse.sharedCorrectingStock.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.correctingStock.model.GetCorrectionByIdUIModelCurrentInventory
import com.example.shared.repository.correctingStock.model.GetWarehouseStockYardsArticlesByIdUIModel
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Enoklit on 11.11.2024.
 */
@HiltViewModel
class CorrStockSharedViewModel @Inject constructor() : BaseViewModel()  {

    var warehouseCode = IncomingConstants.WarehouseParam
    var entryId: Int? = null
    var updadeAmount: Int? = null
    var stockyardId: Int? = null
    var unitCode: String? = null
    var stockyards: List<GetCorrectionByIdUIModelCurrentInventory>? = null
    var updatedActualUnitCode: String? = null
    var articleId: String? = null
    var itemIdsAndArticleIds: List<GetWarehouseStockYardsArticlesByIdUIModel>? = null
    var isItemCorrected: Boolean = false
    var isElementShown: Boolean = false
    var articleForNewDesign: String? = null
    var scannedArticle: ArticlesForDeliveryResponseDTO? = null
    var scannedStockyard: WarehouseStockyardsDTO? = null
    var selectedArticle : WarehouseStockyardInventoryEntriesResponseModel?=null
    var articlesListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null
    var matchCode : String? = null
    var articleDescription : String? = null
    var amount: Any? = null





    fun reset() {
        entryId = null
        amount = null
        updadeAmount = null
        stockyardId = null
        unitCode = null
        stockyards = null
        updatedActualUnitCode = null
        articleId = null
        itemIdsAndArticleIds = null
        scannedArticle = null
        scannedStockyard = null
        matchCode = null
        articleDescription = null
    }

    fun saveAmount(actuellAmount: Any){
        amount = actuellAmount
    }
    fun saveUpdatedAmount(itemAmount: Int){
        updadeAmount = itemAmount
    }
    fun saveEntryId(entryItemId: Int){
        entryId = entryItemId
    }
    fun savestockyardId(stockYardId: Int){
        stockyardId = stockYardId
    }

    fun saveArticleId(id: String) {
        articleId = id
    }
    fun saveMatchCode(id: String) {
        matchCode = id
    }
    fun saveUnitCode(id: String) {
        unitCode = id
    }
    fun saveArticleDescription(id: String) {
        articleDescription = id
    }
    fun saveStockyards(stockyard: List<GetCorrectionByIdUIModelCurrentInventory>) {
        stockyards = stockyard
    }
    fun saveItemIdsAndArticleIds(items: List<GetWarehouseStockYardsArticlesByIdUIModel>) {
        itemIdsAndArticleIds = items
    }
    fun saveArticleForNewDesign(newDesign: String){
        articleForNewDesign = newDesign
    }

}