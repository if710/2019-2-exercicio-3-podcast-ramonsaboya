package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView

class EpisodeDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EPISODE_TITLE = "title"
        const val INTENT_EPISODE_DESCRIPTION = "description"
        const val INTENT_EPISODE_LINK = "link"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        val title = intent.getStringExtra(INTENT_EPISODE_TITLE)
        val description = intent.getStringExtra(INTENT_EPISODE_DESCRIPTION)
        val link = intent.getStringExtra(INTENT_EPISODE_LINK)

        findViewById<TextView>(R.id.item_detail_title).text = title
        findViewById<TextView>(R.id.item_detail_description).text = description
        findViewById<TextView>(R.id.item_detail_link).text = link

        findViewById<TextView>(R.id.item_detail_description).movementMethod =
            ScrollingMovementMethod()
    }

}
