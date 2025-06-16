package com.example.catnicwarehouse.incoming.articles.domain.useCase

import com.example.catnicwarehouse.incoming.articles.domain.repository.ArticleRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryItemRepoModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class FindDeliveryItemsUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {

    operator fun invoke(
        deliveryId: String
    ): Flow<Resource<List<DeliveryItemRepoModel>>> = flow {

        emit(Resource.Loading())

        try {
            val response = articleRepository.findDeliveryItems(deliveryId=deliveryId)

            if (response.isSuccessful) {
                val deliveryItemsList = response.body()
                deliveryItemsList?.let { deliveries ->
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