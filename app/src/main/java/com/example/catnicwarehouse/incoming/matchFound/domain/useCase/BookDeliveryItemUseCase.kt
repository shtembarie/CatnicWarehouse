package com.example.catnicwarehouse.incoming.matchFound.domain.useCase

import com.example.catnicwarehouse.incoming.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookDeliveryItemUseCase @Inject constructor(
    private val matchFoundRepository: MatchFoundRepository
) {

    operator fun invoke(
        deliveryId: String,
        deliveryItemId: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = matchFoundRepository.bookDeliveryItem(
                deliveryId = deliveryId,
                deliveryItemId = deliveryItemId
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