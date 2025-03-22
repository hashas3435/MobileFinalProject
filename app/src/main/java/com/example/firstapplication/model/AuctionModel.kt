package com.example.firstapplication.model

import android.graphics.Bitmap
import android.util.Log
import com.example.firstapplication.base.EmptyCallback

typealias AuctionsListCallback = (List<Auction>) -> Unit
typealias AuctionCallback = (Auction?) -> Unit
typealias UpdateBidCallback = (Boolean) -> Unit

private const val LOG_TAG = "AuctionModel"

class AuctionModel private constructor() {
    private val auctionFirebaseModel = AuctionFirebaseModel()

    companion object {
        val shared = AuctionModel()
    }

    fun getAllAuctions(callback: AuctionsListCallback) {
        auctionFirebaseModel.getAllAuctions(callback)
    }

    fun getAuctionById(auctionId: String, callback: AuctionCallback) {
        auctionFirebaseModel.getAuctionById(auctionId, callback)
    }

    fun addAuction(auction: Auction, itemImage: Bitmap?, callback: EmptyCallback) {
        auctionFirebaseModel.add(auction) { auctionId ->
            if (!auctionId.isNullOrBlank() && itemImage !== null) {
                updateAuctionImage(auctionId, itemImage, callback)
            } else {
                if (auctionId.isNullOrBlank()) {
                    Log.e(LOG_TAG, "failed getting id for the new auction in firestore")
                }
                callback()
            }
        }
    }

    private fun updateAuctionImage(
        auctionId: String,
        image: Bitmap,
        callback: EmptyCallback
    ) {
        uploadImageToFirebase(image, auctionId) { uri ->
            uri?.let {
                auctionFirebaseModel.updateImageUrl(auctionId, uri, callback)
            }
        }
    }

    private fun uploadImageToFirebase(image: Bitmap, name: String, callback: (String?) -> Unit) {
        auctionFirebaseModel.uploadImage(image, name, callback)
    }

    fun placeBid(auctionId: String, bid: Double, callback: UpdateBidCallback) {
        auctionFirebaseModel.placeBid(auctionId, bid, callback)
    }
}