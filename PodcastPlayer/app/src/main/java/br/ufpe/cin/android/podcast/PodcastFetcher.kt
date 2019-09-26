package br.ufpe.cin.android.podcast

import android.os.AsyncTask
import java.net.URL


class PodcastFetcher(val db: AppDatabase, val processItems: (List<ItemFeed>) -> Unit) :
    AsyncTask<String, Int, List<ItemFeed>>() {

    override fun doInBackground(vararg paths: String): List<ItemFeed> {
        var episodes: List<ItemFeed>
        try { // If any RSS parsing fails, retrieve data from DB
            val url = URL(paths[0])
            val feed = url.readText()
            episodes = Parser.parse(feed)
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
