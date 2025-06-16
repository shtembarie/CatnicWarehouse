package com.example.catnicwarehouse.incoming.articles.presentation.sealedClass

import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel

sealed class ArticlesViewState{
    object Reset : ArticlesViewState()
    object Empty : ArticlesViewState()
    object Loading : ArticlesViewState()
    data class Delivery(val delivery: GetDeliveryResponseModel? ) : ArticlesViewState()
    data class ArticlesForDeliveryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : ArticlesViewState()
    data class Error(val errorMessage: String?) : ArticlesViewState()
}
