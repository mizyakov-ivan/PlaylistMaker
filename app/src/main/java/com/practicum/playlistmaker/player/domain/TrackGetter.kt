package com.practicum.playlistmaker.player.domain

import android.content.Intent
import com.practicum.playlistmaker.player.domain.Track

interface TrackGetter {
    fun getTrack(key: String, intent: Intent) : Track
}