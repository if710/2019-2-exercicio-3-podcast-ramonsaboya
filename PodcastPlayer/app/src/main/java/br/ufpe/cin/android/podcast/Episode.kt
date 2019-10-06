package br.ufpe.cin.android.podcast

import androidx.room.*

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "pub_date") val pubDate: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "download_link") val downloadLink: String,
    @ColumnInfo(name = "download_location") var downloadLocation: String? = null,
    @ColumnInfo(name = "current_position") var currentPosition: Int = 0
) {

    companion object {

        fun updateCurrentPosition(newEpisodes: List<Episode>, currentEpisodes: List<Episode>) {
            val positions = HashMap<String, Int>()

            for (episode in currentEpisodes) {
                positions[episode.title] = episode.currentPosition
            }

            for (episode in newEpisodes) {
                episode.currentPosition = positions[episode.title] ?: 0
            }
        }

    }

    override fun toString(): String {
        return title
    }
}
