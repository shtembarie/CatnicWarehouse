package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.useCase

import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.PostDefectiveItemsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Enoklit on 05.12.2024.
 */
class PostArticlesUseCase @Inject constructor(
    private val postDefectiveItemsRepository: PostDefectiveItemsRepository
){
    operator fun invoke(
        postDefectiveArticlesModel: PostDefectiveArticlesModel?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = postDefectiveItemsRepository.postParams(
                postDefectiveArticlesModel = postDefectiveArticlesModel
            )
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            }else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }
        }catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}