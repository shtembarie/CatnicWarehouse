package com.example.shared.repository.supplier.dto

import com.example.shared.base.MapperService
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.repository.supplier.model.SearchedSupplierRepoModel
import javax.inject.Inject

class SupplierNetworkToRepositoryMapper @Inject constructor() :
    MapperService<SearchedVendorDTO, SearchedSupplierRepoModel> {
    override fun mapDomainToDTO(input: SearchedVendorDTO): SearchedSupplierRepoModel =
        input.run {
            SearchedSupplierRepoModel(vendorId?:"", businessPartnerCode?:"", company1?:"")
        }

}