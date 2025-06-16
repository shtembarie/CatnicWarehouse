package com.example.catnicwarehouse.CorrectingStock.stockyards.domain.useCase

import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.repository.GetWarehouseStockYardRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.repository.correctingStock.model.WarehouseStockYardsList
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Enoklit on 07.11.2024.
 */
class GetWarehouseStockYardsUseCase @Inject constructor(
    private val getWarehouseStockYardRepository: GetWarehouseStockYardRepository,

    ) {
    fun getWarehouseStockyard(warehouseCode: String?): Flow<Resource<List<WarehouseStockYardsList>>> = flow {
        emit(Resource.Loading())
        try {
            val response = getWarehouseStockYardRepository.getWarehouseStockYards(warehouseCode)
            if (response.isSuccessful){
                val warehouseStockYard = response.body()
                warehouseStockYard?.let { warehouseStockYards ->
                    emit(Resource.Success(warehouseStockYards))
                }
            } else{
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }
        }  catch (ex : Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }

    }
}