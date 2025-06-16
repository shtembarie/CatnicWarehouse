package com.example.catnicwarehouse.packing.packingItem.domain.useCases

import com.example.catnicwarehouse.packing.addPackingItems.domain.repository.AddPackingItemsRepository
import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.catnicwarehouse.packing.packingItem.domain.repository.PackingItemsRepository
import com.example.catnicwarehouse.packing.packingList.domain.repository.PackingListRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPackingListCommentUseCase @Inject constructor(
    private val packingItemsRepository: PackingItemsRepository
) {

    operator fun invoke(
        id: String?,
    ): Flow<Resource<String?>> = flow {

        emit(Resource.Loading())

        try {
            val response = packingItemsRepository.getPackingListComment(
                id = id
            )
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
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