package br.ufpe.cin.android.podcast

import android.os.AsyncTask
import java.net.URL

class PodcastFetcher(val processItems: (List<Episode>) -> Unit) :
    AsyncTask<String, Int, List<Episode>>() {

    override fun doInBackground(vararg paths: String): List<Episode> {
        var episodes: List<Episode>
        val db = EpisodeDB.getDatabase(MainActivity.applicationContext())

        try { // If any RSS parsing fails, retrieve data from DB
            val url = URL(paths[0])
            val feed = url.readText()
            episodes = FileManager.getInstance().matchDownloadedEpisodes(Parser.parse(feed))

            db.episodeDAO().insertAll(*episodes.toTypedArray())
        } catch (e: Exception) {
            episodes = db.episodeDAO().getAll()
        }

        return episodes
    }

    override fun onPostExecute(items: List<Episode>) {
        processItems(items)
    }

}
