package com.example.shared.networking.network.packing.model.packingList.packingListItem

data class Data(
    val COU_name: String,
    val PAC_CUS_id: String,
    val PAC_ORD_id: String,
    val PAC_Profit_Center: String,
    val PAC_created_by: String,
    val PAC_created_timestamp: String,
    val PAC_delivery_address_city: String,
    val PAC_delivery_address_company1: String,
    val PAC_delivery_address_zip: String,
    val PAC_delivery_date: String,
    val PAC_id: String,
    val PAC_invoice_address_company1: String,
    val PAC_packing_list_date: String,
    val PAC_status: String,
    val packingYear: Any
)