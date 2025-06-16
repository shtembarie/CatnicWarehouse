package com.example.shared.repository.delivery.dto

import com.example.shared.base.MapperService
import com.example.shared.networking.network.delivery.model.DeliveryResponseModel
import com.example.shared.repository.delivery.model.deliveryRepo.DeliveryRepositoryModel
import javax.inject.Inject

class DeliveryNetworkToRepositoryMapper @Inject constructor() :
    MapperService<DeliveryResponseModel, DeliveryRepositoryModel> {
    override fun mapDomainToDTO(input: DeliveryResponseModel): DeliveryRepositoryModel =
        input.run {
            DeliveryRepositoryModel(
                id,
                changedTimeStamp?:"",
                changedBy?:"",
                createdTimeStamp?:"",
                createdBy?:"",
                status?:"",
                vendorId?:"",
                vendorAddressCompany1?:"",
                createdPurchaseOrderId?:"",
                weightInKg?:-1.0
            )
        }
}