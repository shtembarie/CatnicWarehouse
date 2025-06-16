package com.example.catnicwarehouse.incoming.matchFound.domain.useCase

import com.example.catnicwarehouse.incoming.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.purchaseOrder.model.CreateDeliveryItemRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class CreateDeliveryItemUseCase @Inject constructor(
    private val matchFoundRepository: MatchFoundRepository
) {

    operator fun invoke(
        deliveryId: String,
        createDeliveryItemRequestModel: CreateDeliveryItemRequestModel
    ): Flow<Resource<Int>> = flow {

        emit(Resource.Loading())

        try {
            val response = matchFoundRepository.createDeliveryItem(
                deliveryId = deliveryId,
                createDeliveryItemRequestModel = createDeliveryItemRequestModel
            )

            if (response.isSuccessful) {
                val deliveryItemId = response.body()
                emit(Resource.Success(deliveryItemId ?: 0))

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