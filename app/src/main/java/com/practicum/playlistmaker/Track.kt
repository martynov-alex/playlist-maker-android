package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.*

data class Track(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val trackTimeMillis: Int
) {
    val trackTime: String
        get() {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        }
}
