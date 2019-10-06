package br.ufpe.cin.android.podcast

import androidx.room.*

@Dao
interface EpisodeDAO {
    @Query("SELECT * FROM episodes")
    fun getAll(): List<Episode>

    @Query("SELECT * FROM episodes WHERE title LIKE :title")
    fun findByTitle(title: String): Episode

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg episode: Episode)

    @Delete
    fun delete(episode: Episode)

    @Update
    fun updateEpisode(vararg episodes: Episode)
}
