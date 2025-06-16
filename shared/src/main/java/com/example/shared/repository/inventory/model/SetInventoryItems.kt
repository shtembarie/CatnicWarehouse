package com.example.shared.repository.inventory.model

import com.example.shared.repository.inventory.presentation.InventoryDataProvider

data class SetInventoryItems(
    val actualUnitCode: String?,
    val actualStock: Int?,
    val comment: String?
) {
    constructor(
        dataProvider: InventoryDataProvider,
        itemId: Int
    ) : this(
        actualUnitCode = dataProvider.saveUpdatedActualUnitCode(itemId, unitCode = String())
            ?: dataProvider.saveActualUnitCode(actualUnitCode = String())
            ?: dataProvider.saveTargetUnitCode(targetUnitCode = String()),
        actualStock = dataProvider.saveItemAmount(null)
            ?: dataProvider.saveUpdatedItemAmount(itemId, null),
        comment = dataProvider.saveUpdatedComment(itemId, comment = String())
            ?: dataProvider.saveClickedArticleComment(comment = String()) ?: ""
    )
}