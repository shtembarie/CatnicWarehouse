package com.example.catnicwarehouse.defectiveItems.amountFragment.data.repository

import com.example.catnicwarehouse.defectiveItems.amountFragment.domain.repository.UpdateAmountRepository
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import com.example.shared.repository.defectiveArticles.SetAmount
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class UpdateAmountRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
): UpdateAmountRepository {
    override suspend fun updatedeAmount(
        id: Int?,
        setAmount: SetAmount
    ): Response<Unit> {
        return warehouseApiServices.updateAmount(
            id = id,
            setAmount = setAmount
        )
    }

}