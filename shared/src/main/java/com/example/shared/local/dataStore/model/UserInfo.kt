package com.example.shared.local.dataStore.model

import androidx.room.Entity

@Entity
data class UserInfo(
    val accesToken: String,
    val expires_in: Int,
    val token_type: String,
    val refresh_token: String
)
