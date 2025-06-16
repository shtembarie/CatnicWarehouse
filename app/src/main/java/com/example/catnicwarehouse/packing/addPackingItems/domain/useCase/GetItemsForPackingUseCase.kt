package com.example.catnicwarehouse.packing.addPackingItems.domain.useCase

import com.example.catnicwarehouse.packing.addPackingItems.domain.repository.AddPackingItemsRepository
import com.example.catnicwarehouse.packing.matchFound.domain.repository.PackingArticlesRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetItemsForPackingUseCase @Inject constructor(
    private val addPackingItemsRepository: AddPackingItemsRepository
) {

    operator fun invoke(
        packingListId: String
    ): Flow<Resource<List<GetItemsForPackingResponseModelItem>?>> = flow {

        emit(Resource.Loading())

        try {
            val response = addPackingItemsRepository.getItemsForPacking(
                packingListId
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