package com.practicum.playlistmaker.playlist.domain.impl

import com.practicum.playlistmaker.db.domain.api.PlayListDbRepository
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.playlist.domain.api.ExternalNavigatorPlaylist
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val playListDbRepository: PlayListDbRepository,
    private val externalNavigatorPlaylist: ExternalNavigatorPlaylist
) : PlaylistInteractor {

    private suspend fun getAllTrackInPlaylist(): Flow<List<Track>> {
        return playListDbRepository.getAllTrackInPlaylists()
    }

    override suspend fun getTrackInPlaylist(idTracksInPlaylist: String): Flow<List<Track>> = flow {
        val idTracks = playlistRepository.getListIdTracks(idTracksInPlaylist)

        getAllTrackInPlaylist().collect { allTracks ->
            val tracksInPlaylist = allTracks.filter { it.trackId in idTracks }
            emit(tracksInPlaylist)
        }
    }

    override fun sharePlaylist(message: String) {
        externalNavigatorPlaylist.shareLink(message)
    }
}