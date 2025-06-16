package com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.useCase

import com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.repository.GetArticleUnitRepository
import com.example.shared.networking.network.article.ArticleUnitsResponseModel
import com.example.shared.repository.correctingStock.model.ArticleUnitsResponse
import com.example.shared.repository.correctingStock.model.GetArticleUnitsParams
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Enoklit on 19.11.2024.
 */
class GetArticleUnitsUseCase @Inject constructor(
    private val getArticleUnitRepository: GetArticleUnitRepository
){
    operator fun invoke(
        articleId: String,
    ): Flow<Resource<ArticleUnitsResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = getArticleUnitRepository.getArticleUnit(
                articleId = articleId
            )
            if (response.isSuccessful){
                val articleUnits = response.body()
                articleUnits?.let { emit(Resource.Success(it)) }
            }else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    errorBody?.let { JSONObject(it) }
                } catch (e: JSONException) {
                    null
                }

                val errorMessage = if (errorResponse?.has("error") == true) {
                    errorResponse.getString("error")
                } else {
                    "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }

}