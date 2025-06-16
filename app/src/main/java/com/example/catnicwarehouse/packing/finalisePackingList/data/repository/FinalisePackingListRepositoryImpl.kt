package com.example.catnicwarehouse.packing.finalisePackingList.data.repository

import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class FinalisePackingListRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : FinalisePackingListRepository {
    override suspend fun pausePacking(id: String?): Response<Unit> {
        return warehouseApiServices.pausePacking(id)
    }

    override suspend fun finalisePacking(id: String?): Response<Unit> {
        return warehouseApiServices.finalizePackingList(id)
    }

    override suspend fun cancelPacking(
        id: String?,
        cancelPackingRequestModel: CancelPackingRequestModel
    ): Response<Unit> {
        return warehouseApiServices.cancelPackingList(id,cancelPackingRequestModel)
    }

    override suspend fun getDefaultPackingZones(warehouseCode: String?): Response<List<DefaultPackingZoneResultModel>> {
        return warehouseApiServices.getDefaultPackingZones(warehouseCode)
    }




}