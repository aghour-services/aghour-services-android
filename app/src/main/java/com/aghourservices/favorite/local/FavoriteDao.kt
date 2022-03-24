package com.aghourservices.favorite.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.aghourservices.favorite.model.FavoriteEntity

@Dao
interface FavoriteDao {
    @Insert
    fun addData(favoriteEntity: FavoriteEntity?)

    @get:Query("select * from favoritelist")
    val favoriteData: List<FavoriteEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM favoritelist WHERE id=:id)")
    fun isFavorite(id: Int): Int

    @Delete
    fun delete(favoriteEntity: FavoriteEntity?)
}