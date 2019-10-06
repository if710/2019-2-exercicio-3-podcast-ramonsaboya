package br.ufpe.cin.android.podcast

import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileManager(private val activity: AppCompatActivity, private val directory: File) {

    companion object {

        private var INSTANCE: FileManager? = null

        fun create(activity: AppCompatActivity) {
            val directory =
                File(activity.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)!!.path)
            directory.mkdirs()

            synchronized(FileManager::class) {
                INSTANCE = FileManager(activity, directory)
            }
        }

        fun getInstance(): FileManager {
            return INSTANCE!!
        }

    }

    fun matchDownloadedEpisodes(episodes: List<Episode>): List<Episode> {
        for (episode in episodes) {
            val file = File(directory, "${episode.title}.mp3")
            if (file.exists()) {
                episode.downloadLocation = file.absolutePath
            }
        }

        return episodes
    }

    fun fileForEpisodeTitle(title: String): File {
        val file = File(directory.path, "$title.mp3")
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    fun updateEpisodePath(title: String, filePath: String) {
        val db = EpisodeDB.getDatabase(activity.applicationContext)
        val episode = db.episodeDAO().findByTitle(title)
        episode.downloadLocation = filePath
        db.episodeDAO().updateEpisode(episode)
    }

    fun eraseEpisode(episode: Episode) {
        episode.downloadLocation = null
        episode.currentPosition = 0

        val file = File(directory.path, "${episode.title}.mp3")
        if (file.exists()) {
            file.delete()
        }

        val db = EpisodeDB.getDatabase(activity.applicationContext)
        db.episodeDAO().updateEpisode(episode)
    }

}
