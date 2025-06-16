package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.useCase

import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.GetDefectiveArticleByIdRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.defectiveArticles.DefectivesArticlesById
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class GetDefectiveArticleByIdUseCase @Inject constructor(
    private val getDefectiveArticleByIdRepository: GetDefectiveArticleByIdRepository
){
    fun getWarehouseStockyardById(id: Int?): Flow<Resource<DefectivesArticlesById?>> = flow {
        emit(Resource.Loading())
        try {
            val response = getDefectiveArticleByIdRepository.getDefectiveArticleByIdUnit(id)
            if (response.isSuccessful){
                val getArticlesById = response.body()
                getArticlesById.let { articles ->
                    emit(Resource.Success(articles))
                }
            }else{
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }
        }catch (ex : Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}