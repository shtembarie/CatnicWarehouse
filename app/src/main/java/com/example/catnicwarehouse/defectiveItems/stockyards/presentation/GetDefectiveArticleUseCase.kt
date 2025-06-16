package com.example.catnicwarehouse.defectiveItems.stockyards.presentation

import com.example.catnicwarehouse.defectiveItems.stockyards.domain.repository.GetDefectiveArticleRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.defectiveArticles.DefectiveArticleList
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Enoklit on 04.12.2024.
 */
class GetDefectiveArticleUseCase @Inject constructor(
    private val getDefectiveArticleRepository: GetDefectiveArticleRepository
){
    fun getWarehouseStockyard(warehouseCode: String?): Flow<Resource<List<DefectiveArticleList>?>> = flow {
        emit(Resource.Loading())
    try {
        val response = getDefectiveArticleRepository.getDefectiveArticleUnit(warehouseCode)
        if (response.isSuccessful){
            val getArticles = response.body()
            getArticles.let { articles ->
                emit(Resource.Success(articles))
            }
        }else{
            val errorMessage = response.parseError()
            emit(Resource.Error(errorMessage))
        }
    }  catch (ex : Exception){
        ex.printStackTrace()
        emit(Resource.Error(ex.localizedMessage))
    }

    }
}