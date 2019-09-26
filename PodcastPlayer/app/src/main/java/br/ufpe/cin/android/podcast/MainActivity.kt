package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PodcastAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = PodcastAdapter(this, emptyList())
        recyclerView.adapter = adapter

        // Fetch episodes
        PodcastFetcher(db) { handleEpisodes(it) }.execute("https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml")
    }

    private fun handleEpisodes(episodes: List<ItemFeed>) {
        adapter.updateList(episodes)
    }

}

@Database(entities = [ItemFeed::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): ItemFeedDao
}
