package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PodcastAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var podcastPlayerService: PodcastPlayerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.checkAndRequestPermissions()

        val podcastPlayerIntent = Intent(this, PodcastPlayerService::class.java)
        startService(podcastPlayerIntent)
        bindService(podcastPlayerIntent, playerConnection, Context.BIND_AUTO_CREATE)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val t = this
        doAsync {
            sleep(500)
            adapter = PodcastAdapter(t, podcastPlayerService!!, emptyList())
            uiThread {
                recyclerView.adapter = adapter
            }
        }

        // Fetch episodes
        PodcastFetcher(this) { handleEpisodes(it) }.execute("https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml")
    }

    private fun handleEpisodes(episodes: List<ItemFeed>) {
        adapter.updateList(episodes)
    }

    fun updateEpisodePath(title: String, path: String) {
        GlobalScope.launch {
            val db = ItemFeedDB.getDatabase(applicationContext)
            val episode = db.episodeDao().findByTitle(title)
            db.episodeDao().updateTodo(episode.copy(downloadLocation = path))
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded = ArrayList<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 1)
            return false
        }

        return true
    }

    private val playerConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            podcastPlayerService = null
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PodcastPlayerService.PodcastBinder
            podcastPlayerService = binder.service
        }
    }

}