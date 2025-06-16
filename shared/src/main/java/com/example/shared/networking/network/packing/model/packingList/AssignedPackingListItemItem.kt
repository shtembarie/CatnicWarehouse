package com.example.shared.networking.network.packing.model.packingList

data class AssignedPackingListItem(
    val appStatus: String,
    val id: String,
    val index: Int,
    val itemAmount: Int,
    val sumAmountOfItems: Double,
    val deliveryAddressCompany1: String?,
    val deliveryAddressCity: String?,
    val packingListGroupCode: String?,
    val packingListGroupName: String?,
    val packingListGroupPriority: Int,
    val packingListPriority: Int,
    val assignedUserId: String?,
    var isInsideAGroup:Boolean = false
)