package com.example.catnicwarehouse.packing.packingItem.data.repository

import com.example.catnicwarehouse.packing.packingItem.domain.repository.PackingItemsRepository
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class PackingItemsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : PackingItemsRepository {
    override suspend fun getPackingItems(id: String?): Response<PackingItemsModel> {
        return warehouseApiServices.getPackingItems(id)
    }

    override suspend fun startPacking(
        id: String?
    ): Response<Unit> {
        return warehouseApiServices.startPacking(id)
    }

    override suspend fun getPackingListComment(id: String?): Response<String> {
        return warehouseApiServices.getPackingListComment(id)
    }
}