package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemFeed::class], version = 1, exportSchema = false)
abstract  class ItemFeedDB : RoomDatabase() {
    abstract fun episodeDao(): ItemFeedDao

    companion object {
        private var INSTANCE: ItemFeedDB? = null
        fun getDatabase(ctx: Context) : ItemFeedDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFeedDB::class.java,
                        "database"
                    ).fallbackToDestructiveMigration().build()
                }

            }
            return INSTANCE!!
        }


    }
}