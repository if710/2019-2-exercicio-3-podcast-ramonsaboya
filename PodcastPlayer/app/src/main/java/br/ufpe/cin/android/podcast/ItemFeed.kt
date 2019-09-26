package br.ufpe.cin.android.podcast

import androidx.room.*

@Entity(tableName = "episodes")
data class ItemFeed(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "pub_date") val pubDate: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "download_link") val downloadLink: String
) {

    override fun toString(): String {
        return title
    }
}

@Dao
interface ItemFeedDao {
    @Query("SELECT * FROM episodes")
    fun getAll(): List<ItemFeed>

    @Query("SELECT * FROM episodes WHERE title LIKE :title")
    fun findByTitle(title: String): ItemFeed

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg episode: ItemFeed)

    @Delete
    fun delete(episode: ItemFeed)

    @Update
    fun updateTodo(vararg episodes: ItemFeed)
}