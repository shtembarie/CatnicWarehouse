package com.example.shared.repository.movements

data class GetMovementsModelItem(
    val changedBy: String,
    val changedTimestamp: String,
    val createdBy: String,
    val createdTimestamp: String,
    val id: Int,
    val movementItems: List<MovementItem>,
    val status: String
)