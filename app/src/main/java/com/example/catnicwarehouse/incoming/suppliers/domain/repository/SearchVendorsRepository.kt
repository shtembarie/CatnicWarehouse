package com.example.catnicwarehouse.incoming.suppliers.domain.repository

import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import retrofit2.Response

interface SearchVendorsRepository {
    suspend fun searchVendors(
        searchTerm: String?=null
    ): Response<List<SearchedVendorDTO>>

    suspend fun searchCustomers(
        searchTerm: String?=null
    ): Response<List<SearchedCustomerDTOItem>>
}