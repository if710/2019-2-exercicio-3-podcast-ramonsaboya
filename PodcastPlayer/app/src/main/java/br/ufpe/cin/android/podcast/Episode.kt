package br.ufpe.cin.android.podcast

import androidx.room.*

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "pub_date") val pubDate: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "download_link") val downloadLink: String,
    @ColumnInfo(name = "download_location") val downloadLocation: String? = null
) {

    override fun toString(): String {
        return title
    }
}
