package com.example.shared.networking.network.packing.model.packingList

data class PackingListStatusResponseModel(
    val packingListItemDifferences: List<PackingItemDifferences>,
    val replacementPackingListId: String,
    val status: String
)