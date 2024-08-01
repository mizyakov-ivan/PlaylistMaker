package com.practicum.playlistmaker.player.data

import android.content.Intent
import com.practicum.playlistmaker.player.domain.Track
import com.practicum.playlistmaker.player.domain.TrackGetter

class TrackGetterImplementation: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        return intent.getSerializableExtra(key) as Track
    }
}