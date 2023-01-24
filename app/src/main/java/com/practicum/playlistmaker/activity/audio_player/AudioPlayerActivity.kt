package com.practicum.playlistmaker.activity.audio_player

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var track: Track

    private lateinit var backButtonView: TextView
    private lateinit var artworkView: ImageView
    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var trackTimePositionView: TextView
    private lateinit var trackTimeView: TextView
    private lateinit var collectionNameView: TextView
    private lateinit var releaseYearView: TextView
    private lateinit var genreView: TextView
    private lateinit var countryView: TextView

    private lateinit var collectionNameBlockView: Group

    companion object {
        const val ONE_TRACK = "one_track_data"

        fun getIntent(context: Context, track: Track) =
            Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(ONE_TRACK, track)
            }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        initVariables()
        setListeners()

        track = if (savedInstanceState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getSerializable(ONE_TRACK, Track::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getSerializable(ONE_TRACK) as Track
            }
        } else {
            if (intent.extras == null) onBackPressedDispatcher.onBackPressed()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras!!.getSerializable(ONE_TRACK, Track::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                intent.extras!!.getSerializable(ONE_TRACK) as Track
            }
        }

        openTrack(track)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ONE_TRACK, track)
    }

    private fun initVariables() {
        backButtonView = findViewById(R.id.back_button)
        artworkView = findViewById(R.id.artwork)
        trackNameView = findViewById(R.id.track_name)
        artistNameView = findViewById(R.id.artist_name)
        trackTimePositionView = findViewById(R.id.track_time_position)
        trackTimeView = findViewById(R.id.track_time)
        collectionNameView = findViewById(R.id.collection_name)
        releaseYearView = findViewById(R.id.release_year)
        genreView = findViewById(R.id.genre)
        countryView = findViewById(R.id.country)

        collectionNameBlockView = findViewById(R.id.collection_name_block)
    }

    private fun setListeners() {
        backButtonView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openTrack(track: Track) {
        track.run {
            Glide.with(this@AudioPlayerActivity)
                .load(getCoverArtwork())
                .placeholder(R.drawable.ic_search_result_artwork_placeholder)
                .centerCrop()
                .transform(RoundedCorners(20))
                .into(artworkView)
            trackNameView.text = this.trackName
            artistNameView.text = this.artistName
            trackTimePositionView.text = this.trackTime
            trackTimeView.text = this.trackTime
            this.collectionName?.let { collectionNameView.text = this.collectionName } ?: run {
                collectionNameView.text = resources.getString(R.string.audio_player_no_data)
                collectionNameBlockView.visibility = View.GONE
            }
            releaseYearView.text = this.releaseDate?.substring(0, 4) ?: resources.getString(R.string.audio_player_no_data)
            genreView.text = this.primaryGenreName ?: resources.getString(R.string.audio_player_no_data)
            countryView.text = this.country ?: resources.getString(R.string.audio_player_no_data)
        }
    }
}