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

    fun getAllAuctions(callback: AuctionsListCallback) {
        database.collection(Constants.COLLECTIONS.AUCTIONS).get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val auctions: MutableList<Auction> = mutableListOf()
                        for (json in it.result) {
                            val data = json.data
                            data[Auction.ID_KEY] = json.id
                            auctions.add(Auction.fromJSON(data))
                        }
                        callback(auctions)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getAuctionById(auctionId: String, callback: AuctionCallback) {
        database.collection(Constants.COLLECTIONS.AUCTIONS)
            .document(auctionId)
            .get()
            .addOnSuccessListener { document ->
                val data = document.data
                data?.let {
                    it[Auction.ID_KEY] = document.id
                    val auction = Auction.fromJSON(it)
                    callback(auction, null)
                } ?: callback(null, IllegalStateException("auction $auctionId data is null"))
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    fun add(auction: Auction, callback: CreatedDocumentCallback) {
        val auctionDataToAdd = auction.json
        auctionDataToAdd.remove(Auction.ID_KEY)

        database.collection(Constants.COLLECTIONS.AUCTIONS)
            .add(auctionDataToAdd)
            .addOnSuccessListener { documentReference ->
                callback(documentReference.id, null)  // Return the generated document ID
            }
            .addOnFailureListener { e ->
                callback(null, e)  // Return error if something goes wrong
            }
    }

    fun updateImageUrl(auctionId: String, uri: String, callback: NullableExceptionCallback) {
        database.collection(Constants.COLLECTIONS.AUCTIONS)
            .document(auctionId)
            .update(Auction.IMAGE_URL_KEY, uri)
            .addOnSuccessListener {
                callback(null)
            }
            .addOnFailureListener(callback)
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

    fun placeBid(auctionId: String, bid: Double, callback: UpdateBidCallback) {
        val auctionRef = database.collection(Constants.COLLECTIONS.AUCTIONS).document(auctionId)
        database.runTransaction { transaction ->
            val snapshot = transaction.get(auctionRef)
            val currentBid = snapshot.getDouble(Auction.CURRENT_BID_KEY) ?: 0.0

            if (bid > currentBid) {
                transaction.update(auctionRef, Auction.CURRENT_BID_KEY, bid)
                true
            } else {
                false
            }
        }.addOnSuccessListener { success ->
            callback(success, null)
        }.addOnFailureListener { e ->
            callback(false, e)
        }
    }
}
