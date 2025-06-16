package com.example.catnicwarehouse.packing.packingList.domain.useCases

import com.example.catnicwarehouse.packing.packingList.domain.repository.PackingListRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchPackingListsUseCase @Inject constructor(
    private val packingListRepository: PackingListRepository
) {

    operator fun invoke(
        query: String
    ): Flow<Resource<List<SearchPackingListDTO>>> = flow {

        emit(Resource.Loading())

        try {
            val response = packingListRepository.searchPackingLists(query)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
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
