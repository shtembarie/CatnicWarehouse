package com.example.catnicwarehouse.incoming.suppliers.data.repository

import com.example.catnicwarehouse.incoming.suppliers.domain.repository.SearchVendorsRepository
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class SearchVendorsRepositoryImpl @Inject constructor(
    private val apiServices: WarehouseApiServices
) : SearchVendorsRepository {
    override suspend fun searchVendors(searchTerm: String?): Response<List<SearchedVendorDTO>> {
        return apiServices.searchVendors(searchTerm)
    }

    override suspend fun searchCustomers(searchTerm: String?): Response<List<SearchedCustomerDTOItem>> {
        return apiServices.searchCustomers(searchTerm)
    }
}