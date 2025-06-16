package com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers

data class FieldsCollection(
    val fieldsCollection: List<FieldsCollectionX>,
    val filters: List<Filter>,
    val staticFiltersCollection: Any
)