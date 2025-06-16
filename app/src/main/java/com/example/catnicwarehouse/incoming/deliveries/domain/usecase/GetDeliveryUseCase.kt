package com.example.catnicwarehouse.incoming.deliveries.domain.usecase

import com.example.catnicwarehouse.incoming.deliveries.domain.repository.DeliveryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.getDelivery.GetDeliveryResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetDeliveryUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {

    operator fun invoke(
        id: String
    ): Flow<Resource<GetDeliveryResponseModel>> = flow {

        emit(Resource.Loading())

        try {
            val response = deliveryRepository.getDelivery(id=id)
            if (response.isSuccessful) {
                val deliveryResponse = response.body()
                deliveryResponse?.let { delivery ->
                    emit(Resource.Success(delivery))
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