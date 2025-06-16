package com.example.catnicwarehouse.packing.packingItem.domain.repository

import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import retrofit2.Response

interface PackingItemsRepository {
    suspend fun getPackingItems(
        id: String?,
    ): Response<PackingItemsModel>

    suspend fun startPacking(
        id: String?,
    ): Response<Unit>

    suspend fun getPackingListComment(
        id: String?,
    ): Response<String>

}