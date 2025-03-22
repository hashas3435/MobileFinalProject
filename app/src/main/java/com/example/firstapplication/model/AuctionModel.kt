package com.example.firstapplication.model

import android.graphics.Bitmap

typealias AuctionsCallback = (List<Auction>) -> Unit
typealias EmptyCallback = () -> Unit

class AuctionModel private constructor() {
    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = AuctionModel()
    }

    fun getAllAuctions(callback: AuctionsCallback) {
        firebaseModel.getAllAuctions(callback)
    }

    fun addAuction(auction: Auction, itemImage: Bitmap?, callback: EmptyCallback) {
        itemImage?.let { image ->
            uploadImageToFirebase(
                image = image,
                name = auction.id
            ) { url ->
                url?.let {
                    val updatedAuction = auction.copy(imageUrl = it)
                    firebaseModel.add(updatedAuction, callback)
                } ?: callback()
            }
        } ?: firebaseModel.add(auction, callback)
    }

    private fun uploadImageToFirebase(image: Bitmap, name: String, callback: (String?) -> Unit) {
        firebaseModel.uploadImage(image, name, callback)
    }
}