package com.example.catnicwarehouse.incoming.deliveryDetail.domain.useCase

import com.example.catnicwarehouse.incoming.deliveryDetail.domain.repository.DeliveryDetailsRepository

import com.example.catnicwarehouse.utils.parseError
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class CompleteDeliveryWarehousingUseCase @Inject constructor(
    private val deliveryDetailsRepository: DeliveryDetailsRepository
) {

    operator fun invoke(
        deliveryId: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val response = deliveryDetailsRepository.completeDeliveryWarehousing(deliveryId=deliveryId)

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