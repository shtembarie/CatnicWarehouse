package com.example.shared.networking.network.delivery.model.getDelivery

import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO


data class ArticleItem(
    val id: Int,
    val amount: Int,
    val articleId: String,
    val creditNoteId: String,
    val defectiveAmount: Int,
    val defectiveComment: String?,
    val defectiveReason: String?,
    val defectiveUnit: String?,
    val description: String?,
    val invoiceId: String,
    val matchCode: String,
    val purchaseOrderAssignments: List<PurchaseOrderAssignment>,
    val status: String,
    val unitCode: String,
    val warehouseStockYardId: Int,
    val warehouseStockYardName:String?
)


