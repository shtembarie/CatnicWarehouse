package com.example.catnicwarehouse.incoming.articles.data

import com.example.catnicwarehouse.shared.presentation.model.ArticleItemUI
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.getDelivery.ArticleItem

fun mapArticleItemForDeliveryToArticleItemUI(articleItem: ArticlesForDeliveryResponseDTO): ArticleItemUI {
    return ArticleItemUI(
        articleId = articleItem.articleId,
        matchCode = articleItem.matchCode ?: "",
        unitCode = articleItem.unitCode ?: "",
        description = articleItem.description

    )
}

fun mapArticleItemToArticleItemUI(articleItem: ArticleItem): ArticleItemUI {
    return ArticleItemUI(
        id = articleItem.id,
        amount = articleItem.amount,
        defectiveAmount = articleItem.defectiveAmount,
        articleId = articleItem.articleId,
        defectiveComment = articleItem.defectiveComment,
        defectiveReason = articleItem.defectiveReason,
        defectiveUnit = articleItem.defectiveUnit,
        matchCode = articleItem.matchCode,
        unitCode = articleItem.unitCode,
        description = articleItem.description,
        warehouseStockYardId = articleItem.warehouseStockYardId,
        status = articleItem.status
    )
}
