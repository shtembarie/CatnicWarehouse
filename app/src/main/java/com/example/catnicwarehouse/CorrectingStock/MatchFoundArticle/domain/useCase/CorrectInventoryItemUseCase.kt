package com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.useCase

import android.util.Log
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.repository.CorrectInventoryRepository
import com.example.shared.repository.correctingStock.model.CorrectInventoryItems
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Enoklit on 18.11.2024.
 */
class CorrectInventoryItemUseCase @Inject constructor(
    private val correctInventoryRepository: CorrectInventoryRepository
) {
    operator fun invoke(
        warehouseStockYardId: Int?,
        entryId: Int?,
        correctInventoryItems: CorrectInventoryItems
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = correctInventoryRepository.updateCorrectingStockItems(
                warehouseStockYardId = warehouseStockYardId,
                entryId = entryId,
                correctInventoryItems = correctInventoryItems
            )
            if (response.isSuccessful){
                emit(Resource.Success(true))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = try {
                    errorBody?.let { JSONObject(it) }
                } catch (e: JSONException){
                    null
                }
                val errorMessage = if (errorResponse?.has("error") == true){
                    errorResponse.getString("error")
                }else{
                    "Error: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (ex:Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }

    }
}