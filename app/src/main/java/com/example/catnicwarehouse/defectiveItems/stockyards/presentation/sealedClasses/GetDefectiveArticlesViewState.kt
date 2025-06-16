package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.sealedClasses

import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

/**
 * Created by Enoklit on 04.12.2024.
 */
sealed class GetDefectiveArticlesViewState{
    object Reset: GetDefectiveArticlesViewState()
    object Empty: GetDefectiveArticlesViewState()
    object Loading: GetDefectiveArticlesViewState()

    data class Error(val errorMessage: String?) : GetDefectiveArticlesViewState()
    data class DefectiveArticles(val defectiveArticles: List<GetDefectiveArticleUIModel>) : GetDefectiveArticlesViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val titleText:String): GetDefectiveArticlesViewState()

}
