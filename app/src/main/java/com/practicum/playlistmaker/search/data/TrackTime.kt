package com.practicum.playlistmaker.search.data

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.search.data.TrackDto
import java.util.Locale

object TrackTime {
    fun get(track: TrackDto): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
    }
}