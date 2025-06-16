package com.example.catnicwarehouse.packing.shippingContainer.domain.useCase

import com.example.catnicwarehouse.packing.shippingContainer.domain.repository.ShippingContainerRepository
import com.example.catnicwarehouse.utils.parseError
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.tools.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateShippingContainerPackingListItemUseCase @Inject constructor(
    private val shippingContainerRepository: ShippingContainerRepository
) {

    operator fun invoke(
        id: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Flow<Resource<Boolean>?> = flow {

        emit(Resource.Loading())

        try {
            val response = shippingContainerRepository.updateShippingContainerPackingListItem(
                packingListId = id,
                createShippingContainerPackingListItemRequestModel = createShippingContainerPackingListItemRequestModel
            )

            if (response.isSuccessful) {
                emit(Resource.Success(true))
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