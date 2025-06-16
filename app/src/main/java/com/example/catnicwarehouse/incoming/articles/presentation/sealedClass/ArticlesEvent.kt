package com.example.catnicwarehouse.incoming.articles.presentation.sealedClass

import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent

sealed class ArticlesEvent{
    data class GetDelivery(var id: String) : ArticlesEvent()
    data class SearchArticle(val searchTerm: String): ArticlesEvent()
    object Reset : ArticlesEvent()
}
