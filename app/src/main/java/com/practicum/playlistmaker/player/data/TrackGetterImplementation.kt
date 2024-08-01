package com.practicum.playlistmaker.player.data

import android.content.Intent
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.domain.TrackGetter
import com.practicum.playlistmaker.search.data.TrackDto

class TrackGetterImplementation: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        val track = intent.getSerializableExtra(key) as TrackDto
        return Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100,
            track.collectionName,track.releaseDate,track.primaryGenreName,track.country,track.previewUrl)
    }
}