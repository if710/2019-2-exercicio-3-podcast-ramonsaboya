package br.ufpe.cin.android.podcast

import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.net.URL


class PodcastFetcher(val application: MainActivity, val processItems: (List<ItemFeed>) -> Unit) :
    AsyncTask<String, Int, List<ItemFeed>>() {

    override fun doInBackground(vararg paths: String): List<ItemFeed> {
        var episodes: List<ItemFeed>
        val db = ItemFeedDB.getDatabase(application.applicationContext)
        try { // If any RSS parsing fails, retrieve data from DB
            val url = URL(paths[0])
            val feed = url.readText()
            episodes = Parser.parse(feed).toMutableList()

            for (i in 0 until episodes.size) {
                val file = File(
                    application.applicationContext.getExternalFilesDir(
                        Environment.DIRECTORY_PODCASTS
                    )!!.path, "${episodes[i].title}.mp3"
                )
                if (file.exists()) {
                    episodes[i] = episodes[i].copy(downloadLocation = file.absolutePath)
                }
            }

            episodes = episodes.toList()
            db.episodeDao().insertAll(*episodes.toTypedArray())
        }
        catch (e: Exception) {
            episodes = db.episodeDao().getAll()
        }
        return episodes
    }

    override fun onPostExecute(items: List<ItemFeed>) {
        processItems(items)
    }
}
