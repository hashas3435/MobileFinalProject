package com.example.firstapplication.model

import android.util.Log
import com.example.firstapplication.base.Constants
import com.example.firstapplication.base.StringCallback

private const val LOG_TAG = "UserFirebaseModel"

class UserFirebaseModel {
    private val firebaseModel = FirebaseModel()

    fun getUserById(userId: String, callback: UserCallback) {
        firebaseModel.database.collection(Constants.COLLECTIONS.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val data = document.data
                if (data.isNullOrEmpty()) {
                    Log.e(LOG_TAG, "auction $userId data is null")
                } else {
                    val user = User.fromJSON(data, document.id)
                    callback(user)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "failed getting user $userId", exception)
                callback(null)
            }
    }

    fun createUser(userData: Map<String, Any>, authId: String, callback: StringCallback) {
        firebaseModel.database.collection(Constants.COLLECTIONS.USERS).document(authId)
            .set(userData)
            .addOnSuccessListener {
                callback(authId)
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "failed to create user $authId with data $userData", exception)
                callback(null)
            }
    }
}