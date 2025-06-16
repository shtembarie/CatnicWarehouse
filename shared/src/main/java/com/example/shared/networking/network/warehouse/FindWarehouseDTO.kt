package com.example.shared.networking.network.warehouse

data class FindWarehouseDTO(
    val id: Int,
    val name: String,
    val decription: String,
    val defaultLanguage: String,
    val type: String,
    val limitsEnabled: Boolean,
    val articleReserved: Boolean
)
