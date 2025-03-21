package com.example.firstapplication.model

import android.graphics.Bitmap

typealias AuctionsListCallback = (List<Auction>) -> Unit
typealias AuctionCallback = (Auction?, Exception?) -> Unit
typealias CreatedDocumentCallback = (String?, Exception?) -> Unit
typealias UpdateBidCallback = (Boolean, Exception?) -> Unit
typealias NullableExceptionCallback = (Exception?) -> Unit

class AuctionModel private constructor() {
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = AuctionModel()
    }

    fun getAllAuctions(callback: AuctionsListCallback) {
        firebaseModel.getAllAuctions(callback)
    }

    fun getAuctionById(auctionId: String, callback: AuctionCallback) {
        firebaseModel.getAuctionById(auctionId, callback)
    }

    fun addAuction(auction: Auction, itemImage: Bitmap?, callback: NullableExceptionCallback) {
        firebaseModel.add(auction) { auctionId, exception ->
            if (exception !== null) {
                callback(exception)
            } else {
                if (!auctionId.isNullOrBlank() && itemImage !== null) {
                    updateAuctionImage(auctionId, itemImage, callback)
                } else if (auctionId.isNullOrBlank()) {
                    callback(IllegalStateException("failed getting id for the new auction in firestore"))
                } else {
                    callback(null)
                }
            }
        }
    }

    private fun updateAuctionImage(
        auctionId: String,
        image: Bitmap,
        callback: NullableExceptionCallback
    ) {
        uploadImageToFirebase(image, auctionId) { uri ->
            uri?.let {
                firebaseModel.updateImageUrl(auctionId, uri, callback)
            }
        }
    }

    private fun uploadImageToFirebase(image: Bitmap, name: String, callback: (String?) -> Unit) {
        firebaseModel.uploadImage(image, name, callback)
    }

    fun placeBid(auctionId: String, bid: Double, callback: UpdateBidCallback) {
        firebaseModel.placeBid(auctionId, bid, callback)
    }
}