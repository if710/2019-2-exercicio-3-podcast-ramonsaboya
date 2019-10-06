package br.ufpe.cin.android.podcast

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    init {
        INSTANCE = this
    }

    companion object {

        private var INSTANCE: MainActivity? = null

        fun applicationContext(): Context {
            return INSTANCE!!.applicationContext
        }

        fun getActivity(): AppCompatActivity {
            return INSTANCE!!
        }

        fun updateEpisodes(episodes: List<Episode>) {
            INSTANCE!!.episodesAdapter.updateList(episodes)
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
        PreferencesManager.create()

        val podcastPlayerServiceIntent = Intent(this, PodcastPlayerService::class.java)
        startService(podcastPlayerServiceIntent)

        episodesAdapter = PodcastAdapter()

        episodesView = findViewById(R.id.episodes_view)
        episodesView.layoutManager = LinearLayoutManager(this)
        episodesView.adapter = episodesAdapter
        episodesView.setHasFixedSize(true)
        episodesView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        PodcastFetcher().execute(PreferencesManager.getInstance().getFeedURL())

        findViewById<Button>(R.id.config_button).setOnClickListener {
            val intent = Intent(applicationContext, PreferencesActivity::class.java)

            startActivity(intent)
        }

        val interval = PreferencesManager.getInstance().getUpdateInterval()
        val workerRequest = PeriodicWorkRequest.Builder(
            PodcastFetcher.UpdateEpisodes::class.java,
            interval,
            TimeUnit.MINUTES
        ).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(workerRequest)
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindIntent = Intent(this, PodcastPlayerService::class.java)
            isBound = bindService(bindIntent, sConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        if (isBound) {
            unbindService(sConn)
            isBound = false
        }
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
