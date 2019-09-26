package br.ufpe.cin.android.podcast

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button

class PodcastAdapter(private val application: MainActivity, private var episodes: List<ItemFeed>) :
    RecyclerView.Adapter<EpisodeHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlista, parent, false)
        return EpisodeHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeHolder, position: Int) {
        val episode = episodes[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(
                application.applicationContext,
                EpisodeDetailActivity::class.java
            )
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_TITLE, episode.title)
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_DESCRIPTION, episode.description)
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_LINK, episode.link)
            application.startActivity(intent)
        }
        holder.title.text = episode.title
        holder.date.text = episode.pubDate
    }

    override fun getItemCount() = episodes.size

    fun updateList(episodes: List<ItemFeed>) {
        this.episodes = episodes
        notifyItemInserted(episodes.size);
    }

}

class EpisodeHolder(view: View) : RecyclerView.ViewHolder(view) {

    val title: TextView = view.findViewById(R.id.item_title)
    val date: TextView = view.findViewById(R.id.item_date)

}
