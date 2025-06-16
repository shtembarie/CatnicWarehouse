package com.example.catnicwarehouse.sharedinventory.presentation

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.inventory.model.CurrentInventoryWarehouseStockYardsModel
import com.example.shared.repository.inventory.model.InventoryItem
import com.example.shared.repository.inventory.presentation.InventoryDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Enoklit on 24.09.2024.
 */
@HiltViewModel
class InventorySharedViewModel @Inject constructor() : BaseViewModel(), InventoryDataProvider {

    var warehouseCode = IncomingConstants.WarehouseParam
    var warehouseCodes: String? = null
    var articleId: String? = null
    var inventoryItem: InventoryItem? = null
    var itemId: Int? = null
    var totalUpdatedItemAmount: Int? = null
    var clickedStockyardId: Int? = null
    var clickedItemIdActualStock: Int? = null
    var clickedArticleComment: String? = null
    var articlesDescription: String? = null
    var articleMatchCode: String? = null
    var inventoryItemIdsAndArticleIds: List<InventoryItem?>? = null
    var unitCodeActual: String? = null
    var targetUnitCode: String? = null
    var stockyards: List<CurrentInventoryWarehouseStockYardsModel?>? = null
    var updatedActualUnitCode: MutableMap<Int, String> = mutableMapOf()
    var updatedItemComment: MutableMap<Int, String> = mutableMapOf()
    var itemAmount: Int? = null
    var updatedItemAmount: MutableMap<Int, Int> = mutableMapOf()
    var scannedStockyard: WarehouseStockyardsDTO? = null
    var articlesListToSelectFrom :List<InventoryItem>?=null


    fun reset() {
        articlesListToSelectFrom = null
        scannedStockyard = null
        warehouseCodes = null
        articleId = null
        inventoryItem = null
        itemId = null
        totalUpdatedItemAmount = null
        clickedStockyardId = null
        clickedItemIdActualStock = null
        clickedArticleComment = null
        articlesDescription = null
        articleMatchCode = null
        inventoryItemIdsAndArticleIds = null
        unitCodeActual = null
        targetUnitCode = null
        stockyards = null
        updatedActualUnitCode.clear()
        updatedItemComment.clear()
        itemAmount = null
        updatedItemAmount.clear()
    }

    fun saveWarehouseCode(code: String) {
        warehouseCodes = code
    }
    fun savearticleId(articleIds: String) {
        articleId = articleIds
    }
    fun saveClickedItemId(clickedItemId: Int){
        itemId = clickedItemId
    }
    fun saveTotalUpdatedItemAmount(updatedItemAmount: Int){
        totalUpdatedItemAmount = updatedItemAmount
    }
    fun saveClickedStockyardId(inventoryId: Int){
        clickedStockyardId = inventoryId
    }
    fun saveClickedItemIdActualStock(actualStock: Int) {
        clickedItemIdActualStock = actualStock
    }

    fun saveClickedInventoryItem(inventoryItemToUpdate:InventoryItem?){
        inventoryItem = inventoryItemToUpdate
    }
    override fun saveClickedArticleComment(comment: String?): String? {
        clickedArticleComment = comment
        return comment
    }
    override fun saveItemAmount(amount: Int?): Int? {
        itemAmount = amount
        return amount
    }
    override fun saveTargetUnitCode(actualUnitCode: String?): String? {
        targetUnitCode = actualUnitCode
        return actualUnitCode
    }

    override fun saveUpdatedActualUnitCode(itemId: Int?, unitCode: String?): String? {
        return if (itemId != null && unitCode != null) {
            updatedActualUnitCode[itemId] = unitCode
            null
        } else {
            ""
        }
    }
   override fun saveActualUnitCode(actualUnitCode: String?): String? {
        unitCodeActual = actualUnitCode
       return actualUnitCode
    }
    override fun saveUpdatedComment(itemId: Int?, comment: String?): String? {
        return  if (itemId != null && comment != null){
            updatedItemComment[itemId] = comment
            null
        } else {
            ""
        }
    }
   override fun saveUpdatedItemAmount(itemId: Int?, amount: Int?): Int? {
       return if (itemId != null && amount != null) {
           updatedItemAmount[itemId] = amount
           amount
       } else {
           null
       }
    }
    fun saveArticleDescription(articleDescription: String) {
        articlesDescription =  articleDescription
    }
    fun saveArticleMatchCode(articleMatchcode: String) {
        articleMatchCode = articleMatchcode
    }
    fun saveInventoryItemIdsAndArticleIds(items: List<InventoryItem?>) {
        inventoryItemIdsAndArticleIds = items
    }
    fun saveStockyards(stockyard: List<CurrentInventoryWarehouseStockYardsModel>) {
        stockyards = stockyard
    }

}
