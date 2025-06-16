package com.example.catnicwarehouse.packing.addPackingItems.data.repository

import com.example.catnicwarehouse.packing.addPackingItems.domain.repository.AddPackingItemsRepository
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class AddPackingItemsRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : AddPackingItemsRepository {
    override suspend fun getItemsForPacking(packingListId: String): Response<List<GetItemsForPackingResponseModelItem>> {
        return warehouseApiServices.getItemsForPacking(packingListId)
    }


}