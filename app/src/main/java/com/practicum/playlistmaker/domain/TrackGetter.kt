package com.practicum.playlistmaker.domain
import android.content.Intent

interface TrackGetter {
    fun getTrack(ket: String, intent: Intent) : Track
}