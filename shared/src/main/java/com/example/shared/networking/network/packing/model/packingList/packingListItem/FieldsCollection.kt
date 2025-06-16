package com.example.shared.networking.network.packing.model.packingList.packingListItem

data class FieldsCollection(
    val fieldsCollection: List<FieldsCollectionX>,
    val filters: List<Filter>,
    val staticFiltersCollection: Any
)