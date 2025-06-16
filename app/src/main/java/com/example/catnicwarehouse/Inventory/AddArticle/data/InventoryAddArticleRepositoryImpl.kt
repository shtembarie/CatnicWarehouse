package com.example.catnicwarehouse.Inventory.AddArticle.data

import com.example.catnicwarehouse.Inventory.AddArticle.domain.repository.InventoryAddArticleRepository
import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class InventoryAddArticleRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): InventoryAddArticleRepository {
    override suspend fun findInventoryItems(
        id: Int,
        stockyardId: Int,
        command: PostStockyardItemCommand
    ): Response<String> {
        return warehouseApiServices.postItemToStockyard(id, stockyardId, command)
    }

}