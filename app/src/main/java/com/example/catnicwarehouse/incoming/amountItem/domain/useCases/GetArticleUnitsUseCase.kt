package com.example.catnicwarehouse.incoming.amountItem.domain.useCases

import com.example.catnicwarehouse.incoming.amountItem.domain.repository.AmountItemRepository
import com.example.catnicwarehouse.utils.parseError

import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetArticleUnitsUseCase @Inject constructor(
    private val amountItemRepository: AmountItemRepository
) {

    operator fun invoke(
        articleId: String,
    ): Flow<Resource<ArticleUnitsResponseModel>> = flow {

        emit(Resource.Loading())

        try {
            val response = amountItemRepository.getArticleUnits(
                articleId = articleId
            )

            if (response.isSuccessful) {
                val articleUnits = response.body()
                articleUnits?.let { emit(Resource.Success(it)) }
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