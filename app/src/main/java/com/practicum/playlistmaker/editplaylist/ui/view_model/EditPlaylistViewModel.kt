package com.practicum.playlistmaker.editplaylist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.api.PlaylistDbInteractor
import com.practicum.playlistmaker.newplaylist.domain.api.NewPlaylistInteractor
import com.practicum.playlistmaker.newplaylist.domain.model.Playlist
import com.practicum.playlistmaker.newplaylist.ui.view_model.NewPlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val newPlaylistInteractor: NewPlaylistInteractor,
    private val playlistId: Int
) : NewPlaylistViewModel(playlistDbInteractor, newPlaylistInteractor) {

    private val playlistStateLiveData = MutableLiveData<Playlist>()

    fun observeStatePlaylist(): LiveData<Playlist> = playlistStateLiveData

    fun getInfoPlaylist() {
        viewModelScope.launch {
            playlistDbInteractor.getPlaylistById(playlistId).collect { playlistById ->
                playlistStateLiveData.postValue(playlistById)
                if (!playlistById.uriCover.toString().isNullOrEmpty()) uriCover = playlistById.uriCover
            }
        }
    }

    fun editPlaylistClicked(playlistName: String, playlistDescription: String) {
        viewModelScope.launch {
            playlistStateLiveData.value?.let { currentPlaylist ->
                playlistDbInteractor.updatePlaylist(
                    Playlist(
                        id = playlistId,
                        playListName = playlistName,
                        playlistDescription = playlistDescription,
                        uriCover = uriCover,
                        tracksList = currentPlaylist.tracksList,
                        quantityTracks = currentPlaylist.quantityTracks
                    )
                )
            }
        }
    }
}