package com.example.catnicwarehouse.incoming.deliveries.domain.model

data class DeliveryUIModel(
    val title: String, // it's also ID
    val vendorId: String?,
    val supplier: String,
    val date: String,
    val state: String,
    val customerId:String?,
    val type:String?,
    val customerAddressCompany1:String,
    val warehouseCode:String?
)
