package br.ufpe.cin.android.podcast

import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.doAsync
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
        val updatedEpisodes = ArrayList<Episode>()

        for (episode in episodes) {
            val file = File(directory, "${episode.title}.mp3")
            if (file.exists()) {
                updatedEpisodes.add(episode.copy(downloadLocation = file.absolutePath))
            } else {
                updatedEpisodes.add(episode)
            }
        }

        return updatedEpisodes
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
        val episode = db.episodeDAO().findByTitle(title).copy(downloadLocation = filePath)
        db.episodeDAO().updateEpisode(episode)
    }

}
