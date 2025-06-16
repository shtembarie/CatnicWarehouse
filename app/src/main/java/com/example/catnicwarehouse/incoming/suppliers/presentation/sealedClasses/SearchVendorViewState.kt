package com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses

import com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.sealedClasses.UnloadingStockyardsViewState
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO


sealed class SearchVendorViewState {
    object Reset : SearchVendorViewState()
    object Empty : SearchVendorViewState()
    object Loading : SearchVendorViewState()
    data class SearchedVendors(val vendors: List<SearchedVendorDTO>?) : SearchVendorViewState()
    data class ArticlesForDeliveryFound(val articles: List<ArticlesForDeliveryResponseDTO>?) : SearchVendorViewState()
    data class Error(val errorMessage: String?) : SearchVendorViewState()
    data class DeliveryCreated(val data: String?) : SearchVendorViewState()
}