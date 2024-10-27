package com.practicum.playlistmaker.search.ui.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.TimeUtils.formatTrackDuraction
import com.practicum.playlistmaker.player.domain.model.Track

class TrackViewHolder(parentView: ViewGroup, private val resolution: String) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.track_view, parentView, false)
    ) {

    //Ссылки на View-элементы в track_view (View которыми наполнится RecyclerView)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artwork_url_100)
    fun bind(model: Track){
        val roundingRadius = itemView.resources.getDimensionPixelSize(R.dimen.s_padding)
        //Присваивание данных параметрам view-элементов из объекта Track
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = formatTrackDuraction(model.trackTimeMillis.toInt())
        fun setImage(artworkUrl: String) {
            Glide.with(itemView)
                .load(artworkUrl)
                .placeholder(R.drawable.placeholder_35)
                .centerCrop()
                .transform(RoundedCorners(roundingRadius))
                .into(artworkUrl100)
        }

        when (resolution) {
            "high resolution" -> {
                setImage(model.artworkUrl100)
            }

            "low resolution" -> {
                setImage(model.artworkUrl60)
            }
        }
    }
}