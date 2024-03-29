package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var isDarkTheme: Boolean = false

    companion object {
        const val sharedPreferencesFileName = "shared_preferences"
        const val darkThemeKey = "dark_theme"
        const val historyTracksKey = "history_tracks"

        lateinit var sharedPrefs: SharedPreferences
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        initVariables()
        isDarkTheme = sharedPrefs.getBoolean(darkThemeKey, isUsingNightModeResources())
        switchTheme(isDarkTheme)
    }

    fun switchAndSaveTheme(darkThemeEnabled: Boolean) {
        switchTheme(darkThemeEnabled)
        sharedPrefs.edit().putBoolean(darkThemeKey, darkThemeEnabled).apply()
        Log.d("SharedPreferences", "Saved '$darkThemeKey' = '$darkThemeEnabled'")
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun initVariables() {
        // Инициализируем контекст приложения
        appContext = applicationContext

        // Инициализируем SharedPreferences
        sharedPrefs = getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE)
    }
}