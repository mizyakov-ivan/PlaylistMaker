package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.ui.MediaPlayerInteractorImplementation

object Creator {
    fun mediaPlayerProvider(): MediaPlayerInteractor {
        return MediaPlayerInteractorImplementation()
    }
}