package com.example.firstapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Auction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val endDate: Date,
    val imageUrl: String,
    val currentBid: Int
)
