package com.example.shared.local.room.DBManager

import androidx.room.RoomDatabase



abstract class AppDatabase: RoomDatabase() {
    companion object {
        val DATABASE_NAME: String = "CatnicDataBase"
    }
}