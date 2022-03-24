package com.aghourservices.firms

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.json.JSONObject

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
    var isFavorite: Boolean = false

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

    fun toJSONObject(): JSONObject {
        val firmDetails = JSONObject()
        firmDetails.put("id", id)
        firmDetails.put("name", name)
        firmDetails.put("category_id", category_id)
        firmDetails.put("address", address)
        firmDetails.put("description", description)
        firmDetails.put("phone_number", phone_number)
        firmDetails.put("category_name", category_name)
        firmDetails.put("isFavorite", isFavorite)
        return firmDetails
    }

    fun inValid(): Boolean {
        return name.isEmpty() || address.isEmpty() || description.isEmpty() || phone_number.isEmpty()
    }
}