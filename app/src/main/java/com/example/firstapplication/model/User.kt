package com.example.firstapplication.model

class User (
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String
) {
    companion object {
        private const val ID_KEY = "id"
        private const val FULL_NAME_KEY = "fullName"
        private const val EMAIL_KEY = "email"
        private const val PHONE_KEY = "phone"

        fun fromJSON(data: Map<String, Any>, uid: String? = null): User {
            val id = uid ?: data[ID_KEY] as? String ?: ""
            val fullName = data[FULL_NAME_KEY] as? String ?: ""
            val email = data[EMAIL_KEY] as? String ?: ""
            val phone = data[PHONE_KEY] as? String ?: ""

            return User(
                id = id,
                fullName = fullName.trim(),
                email = email.trim(),
                phone = phone
            )
        }
    }

}