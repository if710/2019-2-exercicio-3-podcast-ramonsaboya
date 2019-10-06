package br.ufpe.cin.android.podcast

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun updateEpisodes(episodes: List<Episode>) {
            instance!!.episodesAdapter.updateList(episodes)
        }
    }

    private lateinit var episodesView: RecyclerView
    private lateinit var episodesAdapter: PodcastAdapter

    internal var podcastPlayerService: PodcastPlayerService? = null
    internal var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionManager.create(this)
        FileManager.create(this)

        val podcastPlayerServiceIntent = Intent(this, PodcastPlayerService::class.java)
        startService(podcastPlayerServiceIntent)

        episodesAdapter = PodcastAdapter()

        episodesView = findViewById(R.id.episodes_view)
        episodesView.layoutManager = LinearLayoutManager(this)
        episodesView.adapter = episodesAdapter
        episodesView.setHasFixedSize(true)
        episodesView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        PodcastFetcher { updateEpisodes(it) }.execute("https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml")
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindIntent = Intent(this, PodcastPlayerService::class.java)
            isBound = bindService(bindIntent,sConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(sConn)
        super.onStop()
    }

    private val sConn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            podcastPlayerService = null
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PodcastPlayerService.PodcastBinder
            podcastPlayerService = binder.service
            isBound = true
            episodesAdapter.podcastPlayerService = podcastPlayerService
        }

    }

}
