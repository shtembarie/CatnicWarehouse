package com.example.catnicwarehouse.packing.packingList.domain.repository

import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.PackingModelItem
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.networking.network.packing.model.packingList.packingListItem.PackingListItem
import retrofit2.Response

interface PackingListRepository {
    suspend fun getPackingList(
        id: String?,
    ): Response<PackingModelItem>

    suspend fun getPackingLists(
        inProgress: Boolean?,
    ): Response<PackingListItem>

    suspend fun getAssignedPackingLists(
    ): Response<List<AssignedPackingListItem>>

    suspend fun searchPackingLists(query: String): Response<List<SearchPackingListDTO>>



}