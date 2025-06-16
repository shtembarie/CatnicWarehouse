package com.example.catnicwarehouse.Inventory.matchFoundStockYard.domain.useCase
//
//import com.example.catnicwarehouse.Inventory.matchFoundStockYard.domain.repository.GetInventoryKPIRepository
//import com.example.shared.repository.inventory.model.InventoryKPI
//import com.example.shared.tools.data.Resource
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import org.json.JSONException
//import org.json.JSONObject
//import javax.inject.Inject
//
//class GetInventoryKPIUseCase @Inject constructor(
//    private val getInventoryKPIRepository: GetInventoryKPIRepository,
//    private val  sharedPreferencesHelper: SharedPreferencesHelper
//) {
//    operator fun invoke(
//        id: Int,
//
//        ): Flow<Resource<InventoryKPI>> = flow {
//            emit(Resource.Loading())
//
//        try {
//
//            val savedId = sharedPreferencesHelper.getAllIds()
//            val response = getInventoryKPIRepository.getInventoryKPI(
//                id = (savedId ?: id) as Int
//            )
//            if (response.isSuccessful){
//                val inventoryId = response.body()
//                inventoryId?.let { inventoriesId ->
//                    emit(Resource.Success(inventoriesId))
//
//                }
//            }else{
//                val errorBody = response.errorBody()?.string()
//                val errorResponse = try {
//                    errorBody?.let { JSONObject(it) }
//                } catch (e: JSONException) {
//                    null
//                }
//                val errorMessage = if (errorResponse?.has("error") == true) {
//                    errorResponse.getString("error")
//                } else {
//                    "Error: ${response.code()} - ${response.message()}"
//                }
//                emit(Resource.Error(errorMessage))
//            }
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            emit(Resource.Error(ex.localizedMessage))
//
//        }
//
//    }
//
//}
//
//
