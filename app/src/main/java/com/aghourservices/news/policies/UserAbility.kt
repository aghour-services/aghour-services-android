package com.aghourservices.news.policies

import com.aghourservices.user.User

class UserAbility {
    var user: User

    constructor(user: User) {
        this.user = user
    }

    fun canPublish(): Boolean {
        return user.role == "publisher" || user.role == "admin"
    }
}