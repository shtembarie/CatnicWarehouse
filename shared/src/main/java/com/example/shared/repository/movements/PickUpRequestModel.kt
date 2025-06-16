package com.example.shared.repository.movements

data class PickUpRequestModel(
    val articleId: String?,
    val unitCode: String?,
    val amount: Int?,
    val inventoryEntryId: Int?
)
