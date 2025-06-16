package com.example.catnicwarehouse.incoming.deliveryType.domain.useCase

import com.example.catnicwarehouse.incoming.deliveryType.domain.repository.DeliveryTypeRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class CreateDeliveryUseCase @Inject constructor(
    private val deliveryTypeRepository: DeliveryTypeRepository
) {

    operator fun invoke(
        createDeliveryRequestModel: CreateDeliveryRequestModel
    ): Flow<Resource<String>> = flow {

        emit(Resource.Loading())

        try {
            val response = deliveryTypeRepository.createDelivery(createDeliveryRequestModel=createDeliveryRequestModel)
            if (response.isSuccessful) {
                val createDeliveryResponseModel = response.body()
                createDeliveryResponseModel?.let { emit(Resource.Success(it)) }
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