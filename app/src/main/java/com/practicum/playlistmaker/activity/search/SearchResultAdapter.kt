package com.practicum.playlistmaker.activity.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track

class SearchResultAdapter(
    private val tracks: List<Track>,
    private val openTrack: (Track) -> Unit,
    private val clearHistory: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isHistory: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.search_result_item -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.search_result_item, parent, false
                )
                SearchResultViewHolder.TrackListItemHolder(view)
            }

            R.layout.search_result_placeholder_history_title -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.search_result_placeholder_history_title, parent, false
                )
                SearchResultViewHolder.HistoryTitleHolder(view)
            }

            R.layout.search_result_placeholder_history_clear_button -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.search_result_placeholder_history_clear_button, parent, false
                )
                SearchResultViewHolder.ClearHistoryButtonHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHistory) {
            when (getItemViewType(position)) {
                R.layout.search_result_placeholder_history_title -> {}
                R.layout.search_result_item -> {
                    (holder as SearchResultViewHolder.TrackListItemHolder).bind(
                        tracks[position - 1]
                    )
                    val trackItem: LinearLayout = holder.itemView.findViewById(R.id.search_result_item)
                    trackItem.setOnClickListener { openTrack(tracks[position - 1]) }

                }
                R.layout.search_result_placeholder_history_clear_button -> {
                    val clearHistoryButton: MaterialButton = holder.itemView.findViewById(
                        R.id.search_result_placeholder_history_clear_button
                    )
                    clearHistoryButton.setOnClickListener {
                        clearHistory()
                    }
                }
            }
        } else {
            (holder as SearchResultViewHolder.TrackListItemHolder).bind(tracks[position])
            val trackItem: LinearLayout = holder.itemView.findViewById(R.id.search_result_item)
            trackItem.setOnClickListener { openTrack(tracks[position]) }
        }
    }

    override fun getItemCount(): Int = if (isHistory) tracks.size + 2 else tracks.size

    override fun getItemViewType(position: Int): Int {
        return if (isHistory) {
            when (position) {
                0 -> R.layout.search_result_placeholder_history_title
                tracks.size + 1 -> R.layout.search_result_placeholder_history_clear_button
                else -> R.layout.search_result_item
            }
        } else {
            R.layout.search_result_item
        }
    }

    fun showSearchHistory(show: Boolean) {
        isHistory = show
        notifyDataSetChanged()
    }
}

sealed class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    class TrackListItemHolder(trackListItemView: View) :
        RecyclerView.ViewHolder(trackListItemView) {
        private val trackName: TextView = trackListItemView.findViewById(R.id.item_track_name)
        private val artistName: TextView = trackListItemView.findViewById(R.id.item_artist_name)
        private val trackTime: TextView = trackListItemView.findViewById(R.id.item_track_time)
        private val artwork: ImageView = trackListItemView.findViewById(R.id.item_artwork)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            Glide.with(itemView.context).load(track.artworkUrl100)
                .placeholder(R.drawable.ic_search_result_artwork_placeholder).centerCrop()
                .transform(RoundedCorners(5)).into(artwork)
        }
    }

    class HistoryTitleHolder(historyTitleView: View) : RecyclerView.ViewHolder(historyTitleView) {}
    class ClearHistoryButtonHolder(clearHistoryButtonView: View) :
        RecyclerView.ViewHolder(clearHistoryButtonView) {}
}
