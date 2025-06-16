package com.example.catnicwarehouse.scan.presentation.helper

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO

interface OnArticleSelectListener {
    fun onItemSelected(article: ArticlesForDeliveryResponseDTO)
}
