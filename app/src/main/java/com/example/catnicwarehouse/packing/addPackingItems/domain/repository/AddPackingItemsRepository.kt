package com.example.catnicwarehouse.packing.addPackingItems.domain.repository

import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import retrofit2.Response

interface AddPackingItemsRepository {
    suspend fun getItemsForPacking(
        packingListId: String,
    ): Response<List<GetItemsForPackingResponseModelItem>>
}