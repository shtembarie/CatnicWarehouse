package com.example.catnicwarehouse.defectiveItems.amountFragment.domain.repository

import com.example.shared.repository.defectiveArticles.SetAmount
import retrofit2.Response

/**
 * Created by Enoklit on 05.12.2024.
 */
interface UpdateAmountRepository {
    suspend fun updatedeAmount(
        id:Int?,
        setAmount: SetAmount
    ):Response<Unit>
}