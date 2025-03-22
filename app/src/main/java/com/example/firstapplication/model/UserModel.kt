package com.example.firstapplication.model

import com.example.firstapplication.base.StringCallback

typealias UserCallback = (User?) -> Unit

class UserModel private constructor() {
    private val userFirebaseModel = UserFirebaseModel()
    var user: User? = null

    companion object {
        val shared = UserModel()
    }

    fun getUserById(userId: String, callback: UserCallback) {
        userFirebaseModel.getUserById(userId, callback)
    }

    fun createUser(userData: Map<String, Any>, authId: String, callback: StringCallback) {
        userFirebaseModel.createUser(userData, authId, callback)
    }
}