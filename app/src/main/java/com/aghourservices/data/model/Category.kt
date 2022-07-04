package com.aghourservices.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Category(
    @PrimaryKey
    var id: Int = 0,
    var icon: String? = null,
    var name: String? = null,
) : RealmObject()