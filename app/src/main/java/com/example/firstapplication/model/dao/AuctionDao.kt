package com.example.firstapplication.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.firstapplication.model.Auction

@Dao
interface AuctionDao {
    @Query("SELECT * FROM Auction")
    fun getAll(): List<Auction>

    @Query("SELECT * FROM Auction WHERE id =:id")
    fun getById(id: String): Auction

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAuction(vararg auctions: Auction)

    @Delete
    fun delete(auction: Auction)
}