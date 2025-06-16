package com.example.shared.repository.purchaseOrder.model

data class ValidPurchaseOrderItemRepoModel(
    val deliveredAmount: Int,
    val orderedAmount: Int,
    val purchaseOrderId: String,
    val purchaseOrderItemPosition: String,
    val purchaseOrderItemReference: String,
    val unitCode: String
)