package com.example.catnicwarehouse.CorrectingStock.articles.domain.useCase

import com.example.shared.tools.data.Resource
import com.example.catnicwarehouse.CorrectingStock.articles.domain.repository.GetWarehouseStockYardsArticleRepository
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsArticlesList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Enoklit on 13.11.2024.
 */
class GetStockYardsArticlesUseCase @Inject constructor(
    private val getWarehouseStockYardsArticleRepository: GetWarehouseStockYardsArticleRepository,
) {
    fun getWarehouseStockYardsArticle(warehouseStockYardId: Int?): Flow<Resource<List<WarehouseStockYardsArticlesList>>> = flow {
        emit(Resource.Loading())
        try {
            val response = getWarehouseStockYardsArticleRepository.getStockYardsArticles(warehouseStockYardId)
            if (response.isSuccessful){
                val warehouseStockYardArticles = response.body()
                warehouseStockYardArticles?.let { warehouseStockYardArticles ->
                    emit(Resource.Success(warehouseStockYardArticles))
                }
            } else {
                val  errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    errorBody?.let { JSONObject(it) }
                } catch (e: JSONException){
                    null
                }
                val errorMessage = if (errorResponse?.has("error") == true) {
                    errorResponse.getString("error")
                } else {
                    "Error ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (ex : Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}