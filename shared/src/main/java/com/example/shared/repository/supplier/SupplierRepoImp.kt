package com.example.shared.repository.supplier

import com.example.shared.networking.network.supplier.SupplierNetworkManager
import com.example.shared.repository.supplier.dto.SupplierNetworkToRepositoryMapper
import com.example.shared.repository.supplier.model.SearchedSupplierRepoModel
import com.example.shared.tools.data.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplierRepoImp @Inject constructor(
    private val supplierNetworkToRepositoryMapper: SupplierNetworkToRepositoryMapper,
    private val supplierNetworkManager: SupplierNetworkManager
) : SupplierRepo {
    private val suggestionSupplierList = arrayListOf<SearchedSupplierRepoModel>()
    override fun loadSuppliers(
        scope: CoroutineScope
    ): Flow<DataState<List<SearchedSupplierRepoModel>>> =
        callbackFlow {
            supplierNetworkManager.loadSuppliers(scope).collect { res ->
                res?.let {
                    suggestionSupplierList.clear()
                    suggestionSupplierList.addAll(
                        supplierNetworkToRepositoryMapper.mapDomainToDTO(
                            it
                        )
                    )
                    send(
                        DataState.Success(
                            supplierNetworkToRepositoryMapper.mapDomainToDTO(it)
                        )
                    )
                } ?: kotlin.run {
                    send(DataState.Error(Exception("server error")))
                }
            }
            awaitClose()
        }

    override fun loadSuggestionSupplierList() =
        suggestionSupplierList

    private fun List<SearchedSupplierRepoModel>.filterSuppliers(supplierIds: List<String>) =
        filter { item -> supplierIds.any { it == item.vendorId } }

}