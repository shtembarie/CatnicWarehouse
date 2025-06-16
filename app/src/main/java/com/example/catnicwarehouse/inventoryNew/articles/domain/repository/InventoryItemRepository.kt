package com.example.catnicwarehouse.inventoryNew.articles.domain.repository

import com.example.shared.repository.inventory.model.InventoriseItemResponseModel
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import com.example.shared.repository.inventory.model.InventoryItem
import retrofit2.Response

interface InventoryItemRepository {
    suspend fun getInventoryItems(
        id:String
    ): Response<List<InventoryItem>>

}