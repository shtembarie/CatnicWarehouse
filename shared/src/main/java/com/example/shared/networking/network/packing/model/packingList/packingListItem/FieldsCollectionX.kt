package com.example.shared.networking.network.packing.model.packingList.packingListItem

data class FieldsCollectionX(
    val allowSorting: Boolean,
    val allowedValues: List<AllowedValue>,
    val columnName: String,
    val columnType: String,
    val formatData: Any,
    val formatString: Any,
    val headlineLangKey: String,
    val id: String,
    val isClientVisible: Boolean,
    val isSelected: Boolean,
    val selectPosition: Int,
    val sortedExpression: String
)