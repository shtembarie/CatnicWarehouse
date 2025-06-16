package com.example.domaine.di

import com.example.domain.base.MapperService
import com.example.domain.repository.delivery.model.DeliveryRepositoryModel
import com.example.domaine.usescase.findDelivery.mapper.DeliveryUiModelMapper
import com.example.domaine.usescase.findDelivery.model.DeliveryUiModel
import dagger.Binds

abstract class UiDtoModule {
    @Binds
    abstract fun bindDeliveryUiMapper(deliveryUiModelMapper: DeliveryUiModelMapper):MapperService<DeliveryRepositoryModel,DeliveryUiModel>
}