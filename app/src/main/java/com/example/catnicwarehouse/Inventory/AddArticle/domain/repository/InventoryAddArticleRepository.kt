package com.example.catnicwarehouse.Inventory.AddArticle.domain.repository

import com.example.shared.networking.network.InventoryOrder.dataModel.PostStockyardItemCommand
import retrofit2.Response

interface InventoryAddArticleRepository {
    suspend fun findInventoryItems(
        id: Int,
        stockyardId: Int,
        command: PostStockyardItemCommand
    ): Response<String>
}
