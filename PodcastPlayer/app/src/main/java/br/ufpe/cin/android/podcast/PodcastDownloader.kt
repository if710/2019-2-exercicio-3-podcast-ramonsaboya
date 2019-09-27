package br.ufpe.cin.android.podcast

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import android.widget.Toast

class PodcastDownloader : IntentService("PodcastDownloader") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val path = intent.getStringExtra("url")
        val title = intent.getStringExtra("title")
        val receiver = intent.getParcelableExtra<ResultReceiver>("receiver")
        val bundle = Bundle()
        try {
            val file = File(
                applicationContext.getExternalFilesDir(
                    Environment.DIRECTORY_PODCASTS
                )!!.path
            )
            file.mkdirs()
            val outputFile = File(file.path, "$title.mp3")
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

class ServiceResultReceiver(private val application: MainActivity, handler: Handler) :
    ResultReceiver(handler) {

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        when (resultCode) {
            PodcastDownloader.DOWNLOAD_SUCCESS -> {
                val filePath = resultData.getString("file_path")
                val title = resultData.getString("title")
                application.updateEpisodePath(title!!, filePath!!)
                Toast.makeText(
                    application.applicationContext,
                    "image downloaded via IntentService is successfully: $filePath",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onReceiveResult(resultCode, resultData)
    }

}