package com.example.catnicwarehouse.packing.matchFound.data.repository

import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.catnicwarehouse.packing.matchFound.domain.repository.PackingArticlesRepository
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingList.PackingListStatusResponseModel
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class PackingArticlesRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : PackingArticlesRepository {
    override suspend fun pickAmount(
        packingListId: String?,
        itemId: String?,
        pickAmountRequestModel: PickAmountRequestModel?
    ): Response<Unit> {
        return warehouseApiServices.pickAmount(packingListId,itemId,pickAmountRequestModel)
    }

    override suspend fun changePackedAmount(
        packingListId: String?,
        itemId: String?,
        packedAmount: Int?
    ): Response<Unit> {
        return warehouseApiServices.changePackedAmount(packingListId, itemId, packedAmount)
    }

    override suspend fun getPackingListStatus(packingListId: String?): Response<PackingListStatusResponseModel> {
        return warehouseApiServices.getPackingListStatus(packingListId)
    }


}