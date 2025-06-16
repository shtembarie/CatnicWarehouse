package com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO

sealed class SearchCustomerViewState {
    object Reset : SearchCustomerViewState()
    object Empty : SearchCustomerViewState()
    object Loading : SearchCustomerViewState()
    data class SearchedCustomers(val customers: List<SearchedCustomerDTOItem>? ) : SearchCustomerViewState()
    data class ArticlesForDeliveryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : SearchCustomerViewState()
    data class DeliveryCreated(val createdDelivery: String?) : SearchCustomerViewState()
    data class Error(val errorMessage: String?) : SearchCustomerViewState()
}