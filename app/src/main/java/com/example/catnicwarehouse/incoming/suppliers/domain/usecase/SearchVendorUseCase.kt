package com.example.catnicwarehouse.incoming.suppliers.domain.usecase

import com.example.catnicwarehouse.incoming.suppliers.domain.repository.SearchVendorsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class SearchVendorUseCase @Inject constructor(
    private val searchVendorsRepository: SearchVendorsRepository
) {

    operator fun invoke(
        searchTerm: String? = null,
    ): Flow<Resource<List<SearchedVendorDTO>>> = flow {
        emit(Resource.Loading())

        try {
            val response = searchVendorsRepository.searchVendors(
                searchTerm = searchTerm,
            )
            if (response.isSuccessful) {
                val searchedVendorsList = response.body()
                searchedVendorsList?.let { vendors ->
                    emit(Resource.Success(vendors))
                }
            } else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}