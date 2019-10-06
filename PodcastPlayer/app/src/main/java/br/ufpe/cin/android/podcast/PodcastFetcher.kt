package br.ufpe.cin.android.podcast

import android.content.Context
import android.os.AsyncTask
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jetbrains.anko.runOnUiThread
import java.net.URL

class PodcastFetcher : AsyncTask<String, Int, List<Episode>>() {

    override fun doInBackground(vararg paths: String): List<Episode> {
        var episodes: List<Episode>
        val db = EpisodeDB.getDatabase(MainActivity.applicationContext())

        try { // If any RSS parsing fails, retrieve data from DB
            val url = URL(paths[0])
            val feed = url.readText()
            episodes = FileManager.getInstance().matchDownloadedEpisodes(Parser.parse(feed))

            Episode.updateCurrentPosition(episodes, db.episodeDAO().getAll())

            db.episodeDAO().insertAll(*episodes.toTypedArray())
        } catch (e: Exception) {
            episodes = db.episodeDAO().getAll()
        }

        return episodes
    }

    override fun onPostExecute(items: List<Episode>) {
        MainActivity.updateEpisodes(items)
    }

    class UpdateEpisodes(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

        override fun doWork(): Result {
            applicationContext.runOnUiThread {
                PodcastFetcher().execute(PreferencesManager.getInstance().getFeedURL())
            }

            return Result.success()
        }

    }

}
