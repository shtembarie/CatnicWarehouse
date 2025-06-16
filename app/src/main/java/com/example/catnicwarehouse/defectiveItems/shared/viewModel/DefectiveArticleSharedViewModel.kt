package com.example.catnicwarehouse.defectiveItems.shared.viewModel

import com.example.catnicwarehouse.base.BaseViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Enoklit on 04.12.2024.
 */
@HiltViewModel
class DefectiveArticleSharedViewModel @Inject constructor(): BaseViewModel() {

    var warehouseCode = IncomingConstants.WarehouseParam
    var warehouseStockYardInventoryEntryId: Int? = null
    var unitCode: String? = null
    var defectiveAmount: Int? = null
    var originType: String? = null
    var originObjectId: String? = null
    var reason: String? = null

    var updadeAmount: Int? = null
    var id: Int? = null
    var amount: Int? = null
    var updatedComment: String? = null
    var aktuellComment: String? = null
    var updatedReason: String? = null
    var warehouseName: String? = null

    var defectiveItemIdsAndArticleIds: List<GetDefectiveArticleUIModel?>? = null

    var isItemCorrected: Boolean = false

    var articlesList:ArrayList<WarehouseStockyardInventoryResponseModel>? = null
    var selectedArticle : WarehouseStockyardInventoryEntriesResponseModel?=null
    var articlesListToSelectFrom :List<WarehouseStockyardInventoryEntriesResponseModel>?=null
    var scannedStockyard: WarehouseStockyardsDTO? = null
    var scannedArticle:ArticlesForDeliveryResponseDTO? = null
    var selectedWarehouseStockyardInventoryEntry: WarehouseStockyardInventoryEntriesResponseModel? = null

    var warehouseStockyardInventoryEntry: List<WarehouseStockyardInventoryEntriesResponseModel>? = null

    var selectedDefectiveArticleUIModel: GetDefectiveArticleUIModel? = null




    fun initViewModel(){
        warehouseStockYardInventoryEntryId = null
        unitCode = null
        originType = null
        originObjectId = null
        reason = null
        updadeAmount = null
        amount = null
        updatedComment = null
        aktuellComment = null
        updatedReason = null
        warehouseName = null
        articlesListToSelectFrom = null
        scannedStockyard = null
        defectiveItemIdsAndArticleIds = null
        warehouseStockyardInventoryEntry = null
        selectedWarehouseStockyardInventoryEntry = null
        selectedDefectiveArticleUIModel = null

    }
    fun reset(){
        warehouseStockYardInventoryEntryId = null
        unitCode = null
        originType = null
        originObjectId = null
        reason = null
        updadeAmount = null
        amount = null
        updatedComment = null
        aktuellComment = null
        updatedReason = null
        warehouseName = null
        articlesListToSelectFrom = null
        scannedStockyard = null
        defectiveItemIdsAndArticleIds = null
        warehouseStockyardInventoryEntry = null
    }
    fun saveUpdatedReason(updatedReasons: String?){
        updatedReason = updatedReasons
    }
    fun saveUpdatedAmount(itemAmount: Int){
        updadeAmount = itemAmount
    }
    fun saveUpdatedComment(updatedCommentItemAmount: String){
        updatedComment = updatedCommentItemAmount
    }


}
