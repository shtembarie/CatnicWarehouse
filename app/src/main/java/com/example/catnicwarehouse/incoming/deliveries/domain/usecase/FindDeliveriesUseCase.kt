package com.example.catnicwarehouse.incoming.deliveries.domain.usecase

import com.example.catnicwarehouse.incoming.deliveries.domain.repository.DeliveryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class FindDeliveriesUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {

    operator fun invoke(
        date: String? = null,
        user: String? = null,
        status: String? = null
    ): Flow<Resource<List<DeliveryResponseModel>>> = flow {

        emit(Resource.Loading())

        try {
            val response = deliveryRepository.findDeliveries(
                date = date,
                user = user,
                status = status
            )
            if (response.isSuccessful) {
                val deliveriesList = response.body()
                deliveriesList?.let { deliveries ->
                    emit(Resource.Success(deliveries))
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