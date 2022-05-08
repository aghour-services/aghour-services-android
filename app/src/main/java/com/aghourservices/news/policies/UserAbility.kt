package com.aghourservices.news.policies

import com.aghourservices.user.User

class UserAbility(var user: User) {

    fun canPublish(): Boolean {
        return user.role == "publisher" || user.role == "admin"
    }
}