package com.practicum.playlistmaker.data

import android.content.Intent
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TrackGetter

class TrackGetterImpl: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        return intent.getSerializableExtra(key) as Track
    }
}