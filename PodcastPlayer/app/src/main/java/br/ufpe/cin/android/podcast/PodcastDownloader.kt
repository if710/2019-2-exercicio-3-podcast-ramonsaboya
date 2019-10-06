package br.ufpe.cin.android.podcast

import android.app.IntentService
import android.content.Intent
import android.os.*
import java.io.FileOutputStream
import java.net.URL
import android.widget.Toast
import org.jetbrains.anko.doAsync

class PodcastDownloader : IntentService("PodcastDownloader") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }

        PermissionManager.getInstance().requestPermissions(PermissionManager.EXTERNAL_STORAGE)

        val path = intent.getStringExtra("url")!!
        val title = intent.getStringExtra("title")!!
        val receiver = intent.getParcelableExtra<ResultReceiver>("receiver")
        val bundle = Bundle()
        try {
            val outputFile = FileManager.getInstance().fileForEpisodeTitle(title)
            val url = URL(path)
            val urlConnection = url.openConnection()
            urlConnection.connect()
            val fos = FileOutputStream(outputFile)
            val inputStream = urlConnection.getInputStream()
            val buffer = ByteArray(1024)
            var len1: Int
            while (true) {
                len1 = inputStream.read(buffer)
                if (len1 > 0) {
                    fos.write(buffer, 0, len1)
                } else {
                    break;
                }
            }
            fos.close()
            inputStream.close()
            val filePath = outputFile.path
            bundle.putString("title", title)
            bundle.putString("file_path", filePath)
            receiver?.send(DOWNLOAD_SUCCESS, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val DOWNLOAD_SUCCESS = 11
    }
}

class ServiceResultReceiver(handler: Handler) : ResultReceiver(handler) {

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        when (resultCode) {
            PodcastDownloader.DOWNLOAD_SUCCESS -> {
                val filePath = resultData.getString("file_path")!!
                val title = resultData.getString("title")!!

                doAsync {
                    FileManager.getInstance().updateEpisodePath(title, filePath)

                    val db = EpisodeDB.getDatabase(MainActivity.applicationContext())
                    MainActivity.updateEpisodes(db.episodeDAO().getAll())
                }

                Toast.makeText(
                    MainActivity.applicationContext(),
                    "Podcast downloaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onReceiveResult(resultCode, resultData)
    }

}
