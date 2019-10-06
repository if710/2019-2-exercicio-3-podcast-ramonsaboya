package br.ufpe.cin.android.podcast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import  androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.jetbrains.anko.doAsync
import java.util.concurrent.TimeUnit

class PreferencesActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.preferences_fragment, PreferencesFragment())
            .commit()
    }


    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferencesManager.getInstance().getSharedPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = PreferencesManager.getInstance().getSharedPreferences()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        prefKey: String?
    ) {
        if (prefKey == "feed_url") {
            doAsync {
                EpisodeDB.getDatabase(applicationContext).clearAllTables()
            }
        }

        val interval = PreferencesManager.getInstance().getUpdateInterval()

        val workerRequest = PeriodicWorkRequest.Builder(
            PodcastFetcher.UpdateEpisodes::class.java,
            interval,
            TimeUnit.MINUTES
        ).build()
        val workManager = WorkManager.getInstance(applicationContext)

        workManager.cancelAllWork()
        workManager.enqueue(workerRequest)
    }

    class PreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.activity_preferences, rootKey)
        }
    }

}
