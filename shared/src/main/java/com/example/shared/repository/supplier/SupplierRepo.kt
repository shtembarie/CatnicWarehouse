package com.example.shared.repository.supplier

import com.example.shared.repository.supplier.model.SearchedSupplierRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SupplierRepo {
    fun loadSuppliers(scope: CoroutineScope): Flow<DataState<List<SearchedSupplierRepoModel>>>
    fun loadSuggestionSupplierList():List<SearchedSupplierRepoModel>

}