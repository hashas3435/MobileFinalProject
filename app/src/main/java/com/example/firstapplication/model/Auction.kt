package com.example.firstapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Auction(
    @PrimaryKey val id: String,
    val title: String,
    val endDate: Long,
    val imageUrl: String,
    val currentBid: Int,
    val seller: String
) {
    companion object {
        private const val ID_KEY = "id"
        private const val TITLE_KEY = "title"
        private const val END_DATE_KEY = "endDate"
        private const val IMAGE_URL_KEY = "imageUrl"
        private const val CURRENT_BID_KEY = "currentBid"
        private const val SELLER_KEY = "seller"

        fun fromJSON(json: Map<String, Any>): Auction {
            val id = json[ID_KEY] as? String ?: "0"
            val title = json[TITLE_KEY] as? String ?: ""
            val endDate = json[END_DATE_KEY] as? Long ?: 0
            val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
            val currentBid = json[CURRENT_BID_KEY] as? Int ?: 0
            val seller = json[SELLER_KEY] as? String ?: ""

            return Auction(
                id = id,
                title = title,
                endDate = endDate,
                currentBid = currentBid,
                imageUrl = imageUrl,
                seller = seller
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                TITLE_KEY to title,
                END_DATE_KEY to endDate,
                CURRENT_BID_KEY to currentBid,
                IMAGE_URL_KEY to imageUrl,
                SELLER_KEY to seller
            )
        }
}
