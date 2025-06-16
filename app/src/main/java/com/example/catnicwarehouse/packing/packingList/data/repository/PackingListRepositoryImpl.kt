package com.example.catnicwarehouse.packing.packingList.data.repository

import com.example.catnicwarehouse.packing.packingList.domain.repository.PackingListRepository
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.networking.network.packing.model.packingList.packingListItem.PackingListItem
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class PackingListRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : PackingListRepository {
    override suspend fun getPackingList(id: String?): Response<PackingModelItem> {
        return warehouseApiServices.getPackingList(id)
    }

    override suspend fun getPackingLists(inProgress: Boolean?): Response<PackingListItem> {
        return warehouseApiServices.getPackingLists(inProgress)
    }

    override suspend fun getAssignedPackingLists(): Response<List<AssignedPackingListItem>> {
        return warehouseApiServices.getAssignedPackingLists()
    }

    override suspend fun searchPackingLists(query: String): Response<List<SearchPackingListDTO>> {
        return warehouseApiServices.searchPackingLists(query)
    }


}