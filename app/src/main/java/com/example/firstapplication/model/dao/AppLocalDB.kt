package com.example.firstapplication.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.firstapplication.base.MyApplication
import com.example.firstapplication.model.Auction

@Database(entities = [Auction::class], version = 1)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun auctionDao(): AuctionDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context
            ?: throw IllegalStateException("Application context not available")
        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
