package com.example.firstapplication.model

import android.graphics.Bitmap
import android.util.Log
import com.example.firstapplication.base.Constants
import com.example.firstapplication.base.IsSuccessfulCallback
import com.example.firstapplication.base.StringCallback
import java.io.ByteArrayOutputStream

private const val LOG_TAG = "AuctionFirebaseModel"

class AuctionFirebaseModel {
    private val firebaseModel = FirebaseModel()

    fun getAllAuctions(callback: AuctionsListCallback) {
        firebaseModel.database.collection(Constants.COLLECTIONS.AUCTIONS).get()
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

                    false -> {
                        Log.e(LOG_TAG, "failed fetching auctions list", it.exception)
                        callback(listOf())
                    }
                }
            }
    }

    fun getAuctionById(auctionId: String, callback: AuctionCallback) {
        firebaseModel.database.collection(Constants.COLLECTIONS.AUCTIONS)
            .document(auctionId)
            .get()
            .addOnSuccessListener { document ->
                val data = document.data
                if (data.isNullOrEmpty()) {
                    Log.e(LOG_TAG, "auction $auctionId not found")
                    callback(null)
                } else {
                    data[Auction.ID_KEY] = document.id
                    val auction = Auction.fromJSON(data)
                    callback(auction)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "failed getting auction $auctionId", exception)
                callback(null)
            }
    }

    fun add(auction: Auction, callback: StringCallback) {
        val auctionDataToAdd = auction.json
        auctionDataToAdd.remove(Auction.ID_KEY)

        firebaseModel.database.collection(Constants.COLLECTIONS.AUCTIONS)
            .add(auctionDataToAdd)
            .addOnSuccessListener { documentReference ->
                callback(documentReference.id)  // Return the generated document ID
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "failed to create auction $auction", exception)
                callback(null)  // Return error if something goes wrong
            }
    }

    fun updateImageUrl(auctionId: String, uri: String, callback: IsSuccessfulCallback) {
        firebaseModel.database.collection(Constants.COLLECTIONS.AUCTIONS)
            .document(auctionId)
            .update(Auction.IMAGE_URL_KEY, uri)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e(LOG_TAG, "failed to update image for auction $auctionId", it.exception)
                }
                callback(it.isSuccessful)
            }
    }

    fun uploadImage(image: Bitmap, auctionId: String, callback: StringCallback) {
        val storageRef = firebaseModel.storage.reference
        val imageProfileRef = storageRef.child("images/$auctionId.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageProfileRef.putBytes(data)
        uploadTask
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "failed to upload image for auction $auctionId", exception)
                callback(null)
            }
            .addOnSuccessListener { _ ->
                imageProfileRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        callback(uri.toString())
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            LOG_TAG,
                            "failed downloading image url for auction $auctionId",
                            exception
                        )
                        callback(null)
                    }
            }
    }

    fun placeBid(auctionId: String, bid: Double, callback: IsSuccessfulCallback) {
        val auctionRef =
            firebaseModel.database.collection(Constants.COLLECTIONS.AUCTIONS).document(auctionId)
        firebaseModel.database.runTransaction { transaction ->
            val snapshot = transaction.get(auctionRef)
            val currentBid = snapshot.getDouble(Auction.CURRENT_BID_KEY) ?: 0.0

            if (bid > currentBid) {
                transaction.update(auctionRef, Auction.CURRENT_BID_KEY, bid)
                true
            } else {
                false
            }
        }.addOnSuccessListener { success ->
            callback(success)
        }.addOnFailureListener { exception ->
            Log.e(LOG_TAG, "failed to place bid $bid for auction $auctionId", exception)
            callback(false)
        }
    }
}
