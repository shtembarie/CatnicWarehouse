package com.example.shared.networking.network.supplier

import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SupplierNetworkManager {
    fun loadSuppliers(scope: CoroutineScope):Flow<List<SearchedVendorDTO>?>
}