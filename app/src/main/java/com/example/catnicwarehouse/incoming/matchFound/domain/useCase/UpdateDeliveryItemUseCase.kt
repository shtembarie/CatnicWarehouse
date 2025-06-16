package com.example.catnicwarehouse.incoming.matchFound.domain.useCase

import com.example.catnicwarehouse.incoming.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.updateDeliveryItem.UpdateDeliveryItemRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class UpdateDeliveryItemUseCase @Inject constructor(
    private val matchFoundRepository: MatchFoundRepository
) {

    operator fun invoke(
        deliveryId: String,
        deliveryItemId: String,
        updateDeliveryItemRequestModel: UpdateDeliveryItemRequestModel
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = matchFoundRepository.updateDeliveryItem(
                deliveryId = deliveryId,
                deliveryItemId = deliveryItemId,
                updateDeliveryItemRequestModel = updateDeliveryItemRequestModel
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