package com.example.shared.repository.movements

data class CreateMovementRequest(
    var articleId: String?,
    var unitCode: String?,
    var amount: Int = 0,
    var inventoryEntryId: Int = 0
)
