package com.example.catnicwarehouse.CorrectingStock.articles.presentation.sealedClasses

import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.repository.correctingStock.model.GetWarehouseStockYardsArticlesByIdUIModel

/**
 * Created by Enoklit on 13.11.2024.
 */
sealed class GetWarehouseStockYardsArticleViewState {
    object Reset : GetWarehouseStockYardsArticleViewState()
    object Empty : GetWarehouseStockYardsArticleViewState()
    object Loading : GetWarehouseStockYardsArticleViewState()

    data class Error(val errorMessage: String?) : GetWarehouseStockYardsArticleViewState()
    data class WarehouseStockArticleFound(val warehouseStockYardId: List<GetWarehouseStockYardsArticlesByIdUIModel>) : GetWarehouseStockYardsArticleViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : GetWarehouseStockYardsArticleViewState()
}
