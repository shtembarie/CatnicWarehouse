package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.presentation.sealedClasses

import com.example.shared.repository.correctingStock.model.CorrectInventoryItems

/**
 * Created by Enoklit on 18.11.2024.
 */
sealed class CorrectEvent{
    data class CorrectInventoryAmountUnit(val warehouseStockYardId: Int?, val entryId: Int?, val correctInventoryItems: CorrectInventoryItems) : CorrectEvent()
}
