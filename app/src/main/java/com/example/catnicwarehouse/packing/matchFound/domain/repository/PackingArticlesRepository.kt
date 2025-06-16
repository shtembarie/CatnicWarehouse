package com.example.catnicwarehouse.packing.matchFound.domain.repository

import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingList.PackingListStatusResponseModel
import retrofit2.Response

interface PackingArticlesRepository {
    suspend fun pickAmount(
        packingListId: String?,
        itemId: String?,
        pickAmountRequestModel: PickAmountRequestModel?
    ): Response<Unit>

    suspend fun changePackedAmount(
        packingListId: String?,
        itemId: String?,
        packedAmount: Int?
    ): Response<Unit>

    suspend fun getPackingListStatus(
        packingListId: String?,
    ): Response<PackingListStatusResponseModel>
}