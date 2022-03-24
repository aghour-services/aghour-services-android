package com.aghourservices.favorite.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aghourservices.favorite.model.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}