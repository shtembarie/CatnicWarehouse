package com.example.catnicwarehouse.inventoryNew.matchFound.domain.repository

import com.example.shared.repository.inventory.model.InventoriseItemResponseModel
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import retrofit2.Response

interface InventoryMatchFoundRepository {
    suspend fun inventorizeItem(
        id: String?,
        inventorizeItemRequestModel: InventorizeItemRequestModel?
    ): Response<InventoriseItemResponseModel>
}