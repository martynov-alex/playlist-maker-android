package com.practicum.playlistmaker.domain

import java.text.SimpleDateFormat
import java.util.*

/*
Пример запроса: https://itunes.apple.com/search?entity=song?term=Handlebars
Пример ответа:
{
    "wrapperType":"track",
    "kind":"song",
    "artistId":263337948,
    "collectionId":1440766051,
    "trackId":1440767057,
    "artistName":"Flobots",
    "collectionName":"Fight With Tools",
    "trackName":"Handlebars",
    "collectionCensoredName":"Fight With Tools",
    "trackCensoredName":"Handlebars",
    "artistViewUrl":"https://music.apple.com/us/artist/flobots/263337948?uo=4",
    "collectionViewUrl":"https://music.apple.com/us/album/handlebars/1440766051?i=1440767057&uo=4",
    "trackViewUrl":"https://music.apple.com/us/album/handlebars/1440766051?i=1440767057&uo=4",
    "previewUrl":"https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/5f/c4/22/5fc42280-45e8-06f0-8812-c24313c1f9c9/mzaf_10317061967911371851.plus.aac.p.m4a",
    "artworkUrl30":"https://is3-ssl.mzstatic.com/image/thumb/Music125/v4/d0/d5/f6/d0d5f6c6-f1e2-faca-ec7c-95390b1ce2ea/08UMGIM05628.rgb.jpg/30x30bb.jpg",
    "artworkUrl60":"https://is3-ssl.mzstatic.com/image/thumb/Music125/v4/d0/d5/f6/d0d5f6c6-f1e2-faca-ec7c-95390b1ce2ea/08UMGIM05628.rgb.jpg/60x60bb.jpg",
    "artworkUrl100":"https://is3-ssl.mzstatic.com/image/thumb/Music125/v4/d0/d5/f6/d0d5f6c6-f1e2-faca-ec7c-95390b1ce2ea/08UMGIM05628.rgb.jpg/100x100bb.jpg",
    "collectionPrice":9.99,
    "trackPrice":1.29,
    "releaseDate":"2007-10-16T12:00:00Z",
    "collectionExplicitness":"explicit",
    "trackExplicitness":"notExplicit",
    "discCount":1,
    "discNumber":1,
    "trackCount":12,
    "trackNumber":6,
    "trackTimeMillis":206827,
    "country":"USA",
    "currency":"USD",
    "primaryGenreName":"Hip-Hop/Rap",
    "isStreamable":true
}*/


data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val trackTimeMillis: Int,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?
) {
    val trackTime: String
        get() {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        }

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track

        if (trackId != other.trackId) return false

        return true
    }

    override fun hashCode(): Int {
        return trackId
    }

    override fun toString(): String {
        return "$artistName - $trackName"
    }

}
