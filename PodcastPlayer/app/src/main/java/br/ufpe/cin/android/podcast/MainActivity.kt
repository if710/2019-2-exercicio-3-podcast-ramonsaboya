package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }

    private lateinit var episodesView: RecyclerView
    private lateinit var episodesAdapter: PodcastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionManager.create(this)
        FileManager.create(this)

        episodesAdapter = PodcastAdapter(this, emptyList())

        episodesView = findViewById(R.id.episodes_view)
        episodesView.layoutManager = LinearLayoutManager(this)
        episodesView.adapter = episodesAdapter
        episodesView.setHasFixedSize(true)
        episodesView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        PodcastFetcher { handleEpisodes(it) }.execute("https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml")
    }

    private fun handleEpisodes(episodes: List<Episode>) {
        episodesAdapter.updateList(episodes)
    }

}
