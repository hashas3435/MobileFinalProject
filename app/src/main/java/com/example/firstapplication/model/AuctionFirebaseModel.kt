package com.example.firstapplication.model

import android.graphics.Bitmap
import com.example.firstapplication.base.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class FirebaseModel {
    private val database = Firebase.firestore
    private val storage = Firebase.storage

    init {
        val setting = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }

        database.firestoreSettings = setting
    }

    fun getAllAuctions(callback: AuctionsCallback) {
        database.collection(Constants.COLLECTIONS.AUCTIONS).get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val auctions: MutableList<Auction> = mutableListOf()
                        for (json in it.result) {
                            auctions.add(Auction.fromJSON(json.data))
                        }
                        callback(auctions)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun add(auction: Auction, callback: EmptyCallback) {
        database.collection(Constants.COLLECTIONS.AUCTIONS).document(auction.id)
            .set(auction.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageProfileRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageProfileRef.putBytes(data)
        uploadTask
            .addOnFailureListener { callback(null) }
            .addOnSuccessListener { taskSnapshot ->
                imageProfileRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
    }
}
