package com.example.catnicwarehouse.Inventory.stockyardMatchFound.data.repository

import android.util.Log
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.repository.MatchFoundInventoryRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.inventory.model.SetInventoryItems
import retrofit2.Response
import javax.inject.Inject

class MatchFoundInventoryRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
    ): MatchFoundInventoryRepository {

    override suspend fun setInventoryItemId(
        id: Int?,
        itemId: Int?,
        setInventoryItems: SetInventoryItems?
    ): Response<Unit> {
        Log.d("MatchFoundInventoryRepository", "Sending request with id: $id, itemId: $itemId, setInventoryItems: $setInventoryItems")
        return warehouseApiServices.setInventoryItemId(
            id = id,
            itemId = itemId,
            setInventoryItems = setInventoryItems
        )
    }
}
