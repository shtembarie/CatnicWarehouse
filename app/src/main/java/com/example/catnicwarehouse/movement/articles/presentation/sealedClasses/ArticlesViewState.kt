package com.example.catnicwarehouse.movement.articles.presentation.sealedClasses

import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryResponseModel

sealed class ArticlesViewState{
    object Reset : ArticlesViewState()
    object Empty : ArticlesViewState()
    object Loading : ArticlesViewState()
    data class Error(val errorMessage: String?) : ArticlesViewState()
    data class GetStockyardInventoryResult(val articles: ArrayList<WarehouseStockyardInventoryResponseModel>?) : ArticlesViewState()
    data class ArticleResult(val articles: List<ArticlesForDeliveryResponseDTO>?) : ArticlesViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): ArticlesViewState()
}