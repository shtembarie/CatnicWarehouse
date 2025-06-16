package com.example.catnicwarehouse.shared.presentation.model

import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO


data class ArticleItemUI(
    val id: Int=0,
    val amount: Int=1,
    val articleId: String,
    val defectiveAmount: Int=0,
    val defectiveComment: String?=null,
    val defectiveReason: String?=null,
    val defectiveUnit: String?=null,
    val matchCode: String,
    val unitCode: String,
    val description: String?=null,
    val warehouseStockYardId: Int=0,
    val status:String? = null
)

