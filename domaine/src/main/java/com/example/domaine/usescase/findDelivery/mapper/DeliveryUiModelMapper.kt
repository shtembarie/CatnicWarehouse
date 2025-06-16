package com.example.domaine.usescase.findDelivery.mapper

import com.example.domain.base.MapperService
import com.example.domain.repository.delivery.model.DeliveryRepositoryModel
import com.example.domaine.usescase.findDelivery.model.DeliveryUiModel

class DeliveryUiModelMapper : MapperService<DeliveryRepositoryModel, DeliveryUiModel> {
    override fun mapDomainToDTO(input: DeliveryRepositoryModel): DeliveryUiModel {
        return DeliveryUiModel(input.id,input.createdBy,input.changedTimeStamp,input.status)
    }
}