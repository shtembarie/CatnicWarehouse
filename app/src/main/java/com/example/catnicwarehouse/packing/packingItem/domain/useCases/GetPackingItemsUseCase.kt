package com.example.catnicwarehouse.packing.packingItem.domain.useCases

import com.example.catnicwarehouse.packing.packingItem.domain.repository.PackingItemsRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.packingItem.PackingItemsModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPackingItemsUseCase @Inject constructor(
    private val packingItemsRepository: PackingItemsRepository
) {

    operator fun invoke(
        id: String?,
    ): Flow<Resource<PackingItemsModel>?> = flow {

        emit(Resource.Loading())

        try {
            val response = packingItemsRepository.getPackingItems(
                id = id,
            )

            if (response.isSuccessful) {
                response.body()?.let { emit(Resource.Success(it)) }
            } else {
                val errorMessage = response.parseError()
                emit(Resource.Error(errorMessage))
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage))
        }
    }
}