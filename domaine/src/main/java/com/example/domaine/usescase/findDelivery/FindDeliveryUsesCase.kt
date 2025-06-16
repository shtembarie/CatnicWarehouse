package com.example.domaine.usescase.findDelivery

import com.example.domain.tools.data.DataState
import com.example.domaine.usescase.findDelivery.model.DeliveryUiModel
import kotlinx.coroutines.flow.Flow

interface FindDeliveryUsesCase {
    fun loadDelivery():Flow<DataState<List<DeliveryUiModel>>>
}