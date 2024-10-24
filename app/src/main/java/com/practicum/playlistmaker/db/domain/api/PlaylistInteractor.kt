package com.practicum.playlistmaker.db.domain.api

import com.practicum.playlistmaker.new_playlist.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlayList(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun insertTrackInPlaylist(track: Track, playlist: Playlist, tracksId: List<Int>)
}