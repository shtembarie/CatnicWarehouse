package com.example.catnicwarehouse.inventoryNew.matchFound.data.repository

import com.example.catnicwarehouse.inventoryNew.matchFound.domain.repository.InventoryMatchFoundRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.InventoriseItemResponseModel
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import retrofit2.Response
import javax.inject.Inject

class InventoryMatchFoundRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): InventoryMatchFoundRepository {
    override suspend fun inventorizeItem(
        id: String?,
        inventorizeItemRequestModel: InventorizeItemRequestModel?
    ): Response<InventoriseItemResponseModel> {
        return warehouseApiServices.inventorizeItem(id,inventorizeItemRequestModel)
    }


}