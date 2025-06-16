package com.example.catnicwarehouse.dashboard.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.amountItem.presentation.sealedClass.AmountItemViewState
import com.example.catnicwarehouse.incoming.inventoryItems.data.presentation.sealedClasses.InventoryViewState
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticleUnit
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.dashboard.WarehousesResponseModelItem
import com.example.shared.repository.inventory.model.CurrentInventoryResponseModel
import com.example.shared.repository.inventory.model.InventoryUIModel
import com.example.shared.repository.movements.GetMovementsModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

sealed class DashboardViewState {
    object Reset : DashboardViewState()
    object Empty : DashboardViewState()
    object Loading : DashboardViewState()
    data class Error(val errorMessage: String?) : DashboardViewState()
    data class HasInventories(val hasInventories: Boolean) : DashboardViewState()
    data class GetMovementsResult(val movements: GetMovementsModel?) : DashboardViewState()
    data class CreateMovementResult(val movementId: Int?) : DashboardViewState()
    data class GetWarehousesResult(val warehouses: List<WarehousesResponseModelItem>?) :
        DashboardViewState()

    data class WarehouseStockyardInventoryEntriesResponse(
        val warehouseStockyardInventoryEntriesResponse: ArrayList<WarehouseStockyardInventoryEntriesResponseModel>?,
        val isFromUserEntry: Boolean
    ) : DashboardViewState()

    data class ArticleResult(val articles: List<ArticlesForDeliveryResponseDTO>?) :
        DashboardViewState()

    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) :
        DashboardViewState()

    data class RightsResult(val rights: List<String>?) : DashboardViewState()

}
