package com.example.catnicwarehouse.incoming.inventoryItems.data.domain.usecase


import com.example.catnicwarehouse.incoming.inventoryItems.data.domain.repository.InventoryRepository
import com.example.shared.repository.inventory.model.InventoryResponse
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

//class GetInventoryUseCase @Inject constructor(
//    private val inventoryRepository: InventoryRepository
//    ) {
//    operator fun invoke(
//        warehouseCode: String,
//        status: String
//    ):Flow<Resource<List<InventoryResponse>>> = flow {
//        emit(Resource.Loading())
//
//        try {
//            val response = inventoryRepository.findInventoryItems(warehouseCode=warehouseCode, status = status)
//
//            if (response.isSuccessful){
//                val inventoryItemList = response.body()
//                inventoryItemList?.let { inventories ->
//                    emit(Resource.Success(inventories))
//                }!!
//
//            }else{
//                val errorBody = response.errorBody()?.string()
//                val errorResponse = try {
//                    errorBody?.let { JSONObject(it) }
//                }catch (e:JSONException){
//                    null
//                }
//                val errorMessage = if (errorResponse?.has("error") == true){
//                    errorResponse.getString("error")
//                }else {
//                    "Error: ${response.code()} - ${response.message()}"
//                }
//                emit(Resource.Error(errorMessage))
//            }
//        }catch (ex: Exception) {
//            ex.printStackTrace()
//            emit(Resource.Error(ex.localizedMessage))
//        }
//    }
//}