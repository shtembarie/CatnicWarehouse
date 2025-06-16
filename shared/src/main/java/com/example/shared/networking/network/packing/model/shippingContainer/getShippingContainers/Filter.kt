package com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers

data class Filter(
    val allowedValues: List<Any>,
    val columnName: String,
    val columnType: String,
    val filtreValues: List<String>,
    val formatData: Any,
    val formatString: String,
    val id: String,
    val isActivated: Boolean,
    val isFiltred: Boolean,
    val isVisible: Boolean,
    val position: Int,
    val predicateOperator: String,
    val predicateType: String,
    val reportFieldId: String,
    val reportId: String
)