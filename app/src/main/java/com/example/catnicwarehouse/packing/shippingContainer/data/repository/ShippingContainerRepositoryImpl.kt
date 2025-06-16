package com.example.catnicwarehouse.packing.shippingContainer.data.repository

import com.example.catnicwarehouse.packing.shippingContainer.domain.repository.ShippingContainerRepository
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.GetShippingContainersResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import retrofit2.Response
import javax.inject.Inject

class ShippingContainerRepositoryImpl @Inject constructor(
    private val warehouseApiServices: WarehouseApiServices
) : ShippingContainerRepository {
    override suspend fun getShippingContainers(packingListId: String?): Response<GetShippingContainersResponseModel> {
        return warehouseApiServices.getShippingContainers(packingListId)
    }

    override suspend fun getShippingContainerPackingListItemsByShippingContainer(
        packingListId: String?,
        shippingContainerId: String?
    ): Response<ShippingContainerPackingListItemsByShippingContainerResponseModel> {
        return warehouseApiServices.getShippingContainerPackingListItemsByShippingContainer(
            packingListId,
            shippingContainerId
        )
    }

    override suspend fun getShippingContainerTypes(): Response<List<ShippingContainerTypeResponseModelItem>> {
        return warehouseApiServices.getShippingContainerTypes()
    }

    override suspend fun createShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit> {
        return warehouseApiServices.createShippingContainerPackingListItem(
            packingListId,
            createShippingContainerPackingListItemRequestModel
        )
    }

    override suspend fun updateShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit> {
        return warehouseApiServices.updateShippingContainerPackingListItem(
            packingListId,
            createShippingContainerPackingListItemRequestModel
        )
    }

}