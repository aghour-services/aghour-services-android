package com.aghourservices.data.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class Category : RealmModel {
    @PrimaryKey
    var id: Int = 0

    @Required
    var icon: String = ""

    @Required
    var name: String = ""
}
