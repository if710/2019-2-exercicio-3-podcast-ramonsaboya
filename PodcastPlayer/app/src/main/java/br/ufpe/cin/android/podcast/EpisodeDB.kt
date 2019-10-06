package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Episode::class), version = 1)
abstract class EpisodeDB : RoomDatabase() {
    abstract fun episodeDAO(): EpisodeDAO

    companion object {
        private var INSTANCE: EpisodeDB? = null
        fun getDatabase(ctx: Context): EpisodeDB {
            if (INSTANCE == null) {
                synchronized(EpisodeDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        EpisodeDB::class.java,
                        "episodes.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
