package com.example.shared.repository.defectiveArticles

/**
 * Created by Enoklit on 04.12.2024.
 */
data class DefectiveArticleList(
    val id: Int?,
    val warehouseStockYardInventoryEntryId: Int?,
    val warehouseCode: String?,
    val warehouseName: String?,
    val warehouseStockYardId: Int?,
    val articleId: String?,
    val articleMatchCode: String?,
    val articleDescription: String?,
    val unitCode: String?,
    val defectiveAmount: Int?,
    val restoredAmount: Int?,
    val originType: String?,
    val originObjectId: String?,
    val reportedTimestamp: String?,
    val reportedBy: String?,
    val changedTimestamp: String?,
    val changedBy: String?,
    val reason: String?,
    val comment: String?,
    val status: String?
)
