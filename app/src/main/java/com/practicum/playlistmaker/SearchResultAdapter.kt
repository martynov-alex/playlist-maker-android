package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchResultAdapter(private val tracks: List<Track>) : RecyclerView
.Adapter<SearchResultAdapter.SearchResultHolder>() {

    class SearchResultHolder(searchResultItem: View) : RecyclerView.ViewHolder(searchResultItem) {
        private val trackName: TextView = searchResultItem.findViewById(R.id.item_track_name)
        private val artistName: TextView = searchResultItem.findViewById(R.id.item_artist_name)
        private val trackTime: TextView = searchResultItem.findViewById(R.id.item_track_time)
        private val artwork: ImageView = searchResultItem.findViewById(R.id.item_artwork)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.search_result_artwork_placeholder_icon)
                .centerCrop()
                .transform(RoundedCorners(5))
                .into(artwork)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultHolder {
        val searchResultItem =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_item, parent, false)
        return SearchResultHolder(searchResultItem)
    }

    override fun onBindViewHolder(holder: SearchResultHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

}