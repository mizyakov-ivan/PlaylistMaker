package com.practicum.playlistmaker.util

import android.view.View
import com.practicum.playlistmaker.R

object GetWord {
    fun getWordTrack(count: Int, view: View): String {
        return view.context.resources.getQuantityString(R.plurals.track_count, count, count)
    }

    fun getWordTime(count: Int, view: View): String {
        return view.context.resources.getQuantityString(R.plurals.minute_count, count, count)
    }
}