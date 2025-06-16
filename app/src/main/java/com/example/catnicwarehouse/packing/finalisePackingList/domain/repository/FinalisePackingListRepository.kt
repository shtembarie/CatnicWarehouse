package com.example.catnicwarehouse.packing.finalisePackingList.domain.repository

import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import retrofit2.Response

interface FinalisePackingListRepository {
    suspend fun pausePacking(
        id: String?,
    ): Response<Unit>

    suspend fun finalisePacking(
        id: String?,
    ): Response<Unit>

    suspend fun cancelPacking(
        id: String?,
        cancelPackingRequestModel: CancelPackingRequestModel
    ): Response<Unit>

    suspend fun getDefaultPackingZones(
        warehouseCode:String?
    ): Response<List<DefaultPackingZoneResultModel>>

}