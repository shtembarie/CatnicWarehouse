package com.example.catnicwarehouse.movement.movementList.presentation.sealedClasses

import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.movements.GetMovementsModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class MovementsViewState{
    object Reset : MovementsViewState()
    object Empty : MovementsViewState()
    object Loading : MovementsViewState()
    data class Error(val errorMessage: String?) : MovementsViewState()

    data class GetMovementsResult(val movements: GetMovementsModel?) : MovementsViewState()
    data class ArticleResult(val articles: List<ArticlesForDeliveryResponseDTO>?) : MovementsViewState()
    data class WarehouseStockyardInventoryEntriesResponse(val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?, val isFromUserEntry:Boolean): MovementsViewState()
    data class CloseMovementResult(val isClosed:Boolean?) : MovementsViewState()
}