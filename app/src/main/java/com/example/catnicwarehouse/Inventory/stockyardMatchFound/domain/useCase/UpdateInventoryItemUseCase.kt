package com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.useCase

import android.util.Log
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.repository.MatchFoundInventoryRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.inventory.model.SetInventoryItems
import com.example.shared.tools.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class UpdateInventoryItemUseCase @Inject constructor(
    private val matchFoundInventoryRepository: MatchFoundInventoryRepository
) {

    operator fun invoke(
        id: Int?,
        itemId:Int?,
        setInventoryItems: SetInventoryItems?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = matchFoundInventoryRepository.setInventoryItemId(
                id = id,
                itemId = itemId,
                setInventoryItems = setInventoryItems
            )
            if (response.isSuccessful){
                emit(Resource.Success(true))
            }else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }

        } catch (ex:Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}
