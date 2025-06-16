package com.example.catnicwarehouse.packing.shippingContainer.domain.useCase

import com.example.catnicwarehouse.packing.shippingContainer.domain.repository.ShippingContainerRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetShippingContainerTypesUseCase @Inject constructor(
    private val shippingContainerRepository: ShippingContainerRepository
) {

    operator fun invoke(
    ): Flow<Resource<List<ShippingContainerTypeResponseModelItem>>?> = flow {

        emit(Resource.Loading())

        try {
            val response = shippingContainerRepository.getShippingContainerTypes()

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