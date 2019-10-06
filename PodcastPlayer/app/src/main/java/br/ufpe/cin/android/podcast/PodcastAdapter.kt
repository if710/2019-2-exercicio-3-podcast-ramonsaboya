package br.ufpe.cin.android.podcast

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import java.io.File
import java.util.*

class PodcastAdapter(private var episodes: List<Episode> = emptyList()) :
    RecyclerView.Adapter<EpisodeHolder>() {

    var podcastPlayerService: PodcastPlayerService? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_episode, parent, false)
        return EpisodeHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeHolder, position: Int) {
        val mainActivity = MainActivity.getActivity()

        val episode = episodes[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(
                mainActivity,
                EpisodeDetailActivity::class.java
            )
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_TITLE, episode.title)
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_DESCRIPTION, episode.description)
            intent.putExtra(EpisodeDetailActivity.INTENT_EPISODE_LINK, episode.link)
            mainActivity.startActivity(intent)
        }

        if (episode.downloadLocation == null) {
            holder.action.setImageIcon(Icon.createWithResource(mainActivity, R.drawable.download))
        } else {
            holder.action.setImageIcon(Icon.createWithResource(mainActivity, R.drawable.play))
        }

        holder.title.text = episode.title
        holder.date.text = episode.pubDate
        holder.action.setOnClickListener {
            if (episode.downloadLocation == null) {
                val url = episode.downloadLink
                val intent = Intent(mainActivity, PodcastDownloader::class.java)
                intent.putExtra("title", episode.title)
                intent.putExtra(
                    "receiver",
                    ServiceResultReceiver(Handler())
                )
                intent.putExtra("url", url)
                mainActivity.startService(intent)
            } else {
                podcastPlayerService?.play(episode)
            }
        }
    }

    override fun getItemCount() = episodes.size

    fun updateList(episodes: List<Episode>) {
        if (this.episodes.isEmpty()) {
            this.episodes = episodes
            notifyItemInserted(episodes.size)
        } else {
            this.episodes = episodes
            notifyItemRangeChanged(0, episodes.size)
        }
    }

}

class EpisodeHolder(view: View) : RecyclerView.ViewHolder(view) {

    val action: ImageView = view.findViewById(R.id.item_action)
    val title: TextView = view.findViewById(R.id.item_title)
    val date: TextView = view.findViewById(R.id.item_date)

}
