package com.aghourservices.data.model

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import org.json.JSONObject

@RealmClass
open class Firm : RealmObject() {
    @PrimaryKey
    var id: Int = 0

    @Index
    var category_id: Int = 0

    @Required
    var name: String = ""

    @Required
    var address: String = ""

    @Required
    var description: String = ""

    @Required
    var phone_number: String = ""

    @Required
    var category_name: String = ""
    var isFavorite: Boolean = false

    //Google jsonObject
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

    // Android Sdk JSONObject
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