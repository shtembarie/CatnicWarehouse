package com.example.catnicwarehouse.packing.matchFound.domain.useCases

import com.example.catnicwarehouse.packing.matchFound.domain.repository.PackingArticlesRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangePackingAmountUseCase  @Inject constructor(
    private val packingArticlesRepository: PackingArticlesRepository
) {

    operator fun invoke(
        packingListId: String?,
        itemId: String?,
        packedAmount: Int?
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = packingArticlesRepository.changePackedAmount(
                packingListId,
                itemId, packedAmount
            )
            if (response.isSuccessful) {
                emit(Resource.Success(true))
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