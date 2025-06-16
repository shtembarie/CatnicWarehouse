package com.example.shared.repository.defectiveArticles

/**
 * Created by Enoklit on 05.12.2024.
 */
data class PostDefectiveArticlesModel(
    var warehouseStockYardInventoryEntryId: Int?,
    var amount: Int?,
    var unitCode: String?,
    var reason: String?,
    var comment: String?,
    var originType: String?,
    var originObjectId: String?,
)
{


}