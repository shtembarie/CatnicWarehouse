package com.example.catnicwarehouse.packing.shippingContainer.domain.repository

import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.ShippingContainerPackingListItemsByShippingContainerResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.createShippingContainerPackingListItem.CreateShippingContainerPackingListItemRequestModel
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.GetShippingContainersResponseModel
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import retrofit2.Response

interface ShippingContainerRepository {
    suspend fun getShippingContainers(
        packingListId: String?,
    ): Response<GetShippingContainersResponseModel>

    suspend fun getShippingContainerPackingListItemsByShippingContainer(
        packingListId: String?, shippingContainerId: String?
    ): Response<ShippingContainerPackingListItemsByShippingContainerResponseModel>

    suspend fun getShippingContainerTypes(
    ): Response<List<ShippingContainerTypeResponseModelItem>>

    suspend fun createShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit>

    suspend fun updateShippingContainerPackingListItem(
        packingListId: String?,
        createShippingContainerPackingListItemRequestModel: CreateShippingContainerPackingListItemRequestModel
    ): Response<Unit>
}