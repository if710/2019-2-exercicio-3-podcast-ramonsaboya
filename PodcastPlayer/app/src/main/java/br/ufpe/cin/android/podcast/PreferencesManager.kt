package br.ufpe.cin.android.podcast

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.lang.Long.parseLong

class PreferencesManager {

    companion object {

        private var INSTANCE: PreferencesManager? = null

        fun create() {
            synchronized(PreferencesManager::class) {
                INSTANCE = PreferencesManager()
            }
        }

        fun getInstance(): PreferencesManager {
            return INSTANCE!!
        }

    }

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(MainActivity.applicationContext())
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences!!
    }

    fun getFeedURL(): String {
        return sharedPreferences?.getString(
            "feed_url",
            MainActivity.applicationContext().getString(R.string.default_feed_url)
        )!!
    }

    fun getUpdateInterval(): Long {
        val stringValue = sharedPreferences?.getString(
            "update_interval",
            MainActivity.applicationContext().getString(R.string.default_update_interval)
        )!!
        return parseLong(stringValue)
    }

}
