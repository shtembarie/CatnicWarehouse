package com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.repository

import com.example.shared.repository.inventory.model.SetInventoryItems
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response

interface MatchFoundInventoryRepository {

    suspend fun setInventoryItemId(
        id: Int?,
        itemId: Int?,
        setInventoryItems: SetInventoryItems?
    ): Response<Unit>
}
