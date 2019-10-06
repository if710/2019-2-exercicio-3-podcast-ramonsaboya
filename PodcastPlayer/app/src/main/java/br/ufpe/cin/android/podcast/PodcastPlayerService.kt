package br.ufpe.cin.android.podcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.FileInputStream

class PodcastPlayerService : Service() {

    companion object {
        private const val PLAY_ACTION = "br.ufpe.cin.android.podcast.PlayAction"
    }

    private lateinit var mPlayer: MediaPlayer
    private val mBinder = PodcastBinder()

    private var currentEpisode: Episode? = null

    override fun onCreate() {
        super.onCreate()

        mPlayer = MediaPlayer()
        mPlayer.isLooping = true
        mPlayer.setOnCompletionListener {
            if (currentEpisode != null) {
                doAsync {
                    FileManager.getInstance().eraseEpisode(currentEpisode!!)
                    val episodes =
                        EpisodeDB.getDatabase(MainActivity.applicationContext()).episodeDAO()
                            .getAll()
                    uiThread {
                        MainActivity.updateEpisodes(episodes)
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel("1", "notification", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val notificationIntent = Intent(applicationContext, PodcastPlayerService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val playIntent = Intent(PLAY_ACTION)
        val playPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, playIntent, 0)

        val notification = NotificationCompat.Builder(
            applicationContext, "1"
        )
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(NotificationCompat.Action(R.drawable.play, "Play/Pause", playPendingIntent))
            .setOngoing(true)
            .setContentTitle("PodcastPlayer")
            .setContentIntent(pendingIntent).build()

        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val intentFilter = IntentFilter(PLAY_ACTION)
        registerReceiver(receiver, intentFilter)

        return START_STICKY
    }

    override fun onDestroy() {
        saveProgress()
        mPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    fun play(episode: Episode) {
        if (episode != currentEpisode) {
            saveProgress()

            val fis = FileInputStream(episode.downloadLocation!!)
            mPlayer.reset()
            mPlayer.setDataSource(fis.fd)
            mPlayer.prepare()
            mPlayer.seekTo(episode.currentPosition)
            fis.close()

            currentEpisode = episode

            mPlayer.start()
        } else {
            if (!mPlayer.isPlaying) {
                mPlayer.start()
            } else {
                mPlayer.pause()
                saveProgress()
            }
        }
    }

    private fun saveProgress() {
        if (currentEpisode != null) {
            val currPos = mPlayer.currentPosition

            currentEpisode!!.currentPosition = currPos

            doAsync {
                val db = EpisodeDB.getDatabase(applicationContext)

                db.episodeDAO().updateEpisode(currentEpisode!!)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (currentEpisode != null && action == PLAY_ACTION) {
                play(currentEpisode!!)
            }
        }
    }

    inner class PodcastBinder : Binder() {
        internal val service: PodcastPlayerService
            get() = this@PodcastPlayerService
    }

}
