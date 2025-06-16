package com.example.catnicwarehouse.CorrectingStock.stockyards.domain.sealedClasses

import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.repository.correctingStock.model.GetCorrectionByIdUIModelCurrentInventory
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsList
import com.example.shared.repository.inventory.model.GetInventoryByIdUIModelCurrentInventory

/**
 * Created by Enoklit on 07.11.2024.
 */
sealed class GetWarehouseStockYardViewState {
    object Reset : GetWarehouseStockYardViewState()
    object Empty : GetWarehouseStockYardViewState()
    object Loading : GetWarehouseStockYardViewState()

    data class Error(val errorMessage: String?) : GetWarehouseStockYardViewState()
    data class WarehouseStockFound(val warehouseStockyard: List<GetCorrectionByIdUIModelCurrentInventory>) : GetWarehouseStockYardViewState()
    data class ArticlesForInventoryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : GetWarehouseStockYardViewState()
    data class WarehouseStockByIdFound(val warehouseStockyard: WarehouseStockyardsDTO?) : GetWarehouseStockYardViewState()

}