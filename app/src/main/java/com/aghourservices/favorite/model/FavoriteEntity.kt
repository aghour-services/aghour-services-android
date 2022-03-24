package com.aghourservices.favorite.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritelist")
class FavoriteEntity {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var address: String = ""
    var description: String = ""
    var phone_number: String = ""
}
