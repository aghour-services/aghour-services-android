package com.aghourservices.firms

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


@RealmClass
open class Firm : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var category_id: Int = 0
    var name: String = ""
    var address: String = ""
    var description: String = ""
    var phone_number: String = ""
    var category_name: String = ""

    fun toJsonObject(): JsonObject {
        val firmObject = JsonObject()
        val firmDetails = JsonObject()
        firmDetails.addProperty("name", name)
        firmDetails.addProperty("category_id", category_id)
        firmDetails.addProperty("address", address)
        firmDetails.addProperty("description", description)
        firmDetails.addProperty("phone_number", phone_number)

        firmObject.add("firm", firmDetails)
        return firmObject
    }

    fun inValid(): Boolean {
        return name.isEmpty() || address.isEmpty() || description.isEmpty() || phone_number.isEmpty()
    }
}