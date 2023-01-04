package com.practicum.playlistmaker.activity.search

import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track

class SearchHistory {
    var historyTracks = ArrayList<Track>()

    private val historyTracksKey = App.appContext.getString(R.string.history_tracks_key)

    fun loadHistory() {
        historyTracks.clear()
        val json = App.sharedPrefs.getString(historyTracksKey, null)
        if (json != null) {
            val tracks = Gson().fromJson(json, Array<Track>::class.java)
            historyTracks.addAll(tracks)
        }
        Log.d("SearchHistory", "Tracks loaded: $historyTracks")
    }

    fun saveHistory() {
        val json = Gson().toJson(historyTracks)
        App.sharedPrefs.edit().putString(historyTracksKey, json).apply()
        Log.d("SearchHistory", "Tracks saved: $historyTracks")
    }

    fun addTrack(track: Track) {
        if (historyTracks.contains(track)) historyTracks.remove(track)
        if (historyTracks.size == 10) historyTracks.removeFirst()
        historyTracks.add(track)

        Log.d("SearchHistory", "Add $track")
    }

    fun clearHistory() {
        historyTracks.clear()
        App.sharedPrefs.edit().remove(historyTracksKey).apply()
        Log.d("SearchHistory", "Clear search history")
    }
}