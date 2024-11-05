package com.practicum.playlistmaker.playlist.ui.view_model

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.api.PlaylistDbInteractor
import com.practicum.playlistmaker.newplaylist.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.ui.models.SharePlaylistStateInterface
import com.practicum.playlistmaker.util.TimeUtils
import kotlinx.coroutines.launch
import java.util.Locale

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistDbInteractor: PlaylistDbInteractor,
    private val playlistId: Int,
) : ViewModel() {

    var playlist: Playlist? = null
    private var trackInPlaylist = listOf<Track>()


    private val playlistStateLiveData = MutableLiveData<Playlist>()
    private val tracksInPlaylistStateLiveData = MutableLiveData<List<Track>>()
    private val allTimeTracksStateLiveData = MutableLiveData<String>()
    private val quantityTracksStateLiveData = MutableLiveData<Int>()
    private val sharePlaylistStateLiveData = MutableLiveData<SharePlaylistStateInterface>()

    fun observeStatePlaylist(): LiveData<Playlist> = playlistStateLiveData
    fun observeStateTracksInPlaylist(): LiveData<List<Track>> = tracksInPlaylistStateLiveData
    fun observeStateAllTimeTracks(): LiveData<String> = allTimeTracksStateLiveData
    fun observeStateQuantityTracks(): LiveData<Int> = quantityTracksStateLiveData
    fun observeStateSharePlaylist(): LiveData<SharePlaylistStateInterface> =
        sharePlaylistStateLiveData

    fun getPlaylist() {
        viewModelScope.launch {
            playlistDbInteractor.getPlaylistById(playlistId).collect { playlistById ->
                playlist = playlistById
                playlist?.let {
                    playlistStateLiveData.postValue(it)
                    getTrackInPlaylist(it)
                }
            }
        }
    }

    private fun getTrackInPlaylist(playlist: Playlist) {
        if (playlist.tracksList.isNullOrEmpty()) {
            getTrackTime(emptyList())
            getQuantityTrack(0)
            return
        }
        viewModelScope.launch {
            playlist.tracksList?.let { trackList ->
                playlistInteractor.getTrackInPlaylist(trackList).collect { tracks ->
                    trackInPlaylist = tracks
                    tracksInPlaylistStateLiveData.postValue(tracks.reversed())
                    getTrackTime(tracks)
                    getQuantityTrack(playlist.quantityTracks)
                }
            }
        }
    }

    private fun getTrackTime(trackInPlaylist: List<Track>?) {
        var trackTime = 0
        if (trackInPlaylist!!.isEmpty()) trackTime = 0
        else {
            trackInPlaylist.forEach{ track ->
                trackTime += track.trackTimeMillis.toInt()
            }
        }

        val trackTimeMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(trackTime)

        if (trackTimeMinutes.toInt() < 10) allTimeTracksStateLiveData.postValue(
            trackTimeMinutes.substring(
                1
            )
        )
        else allTimeTracksStateLiveData.postValue(trackTimeMinutes)
    }

    private fun getQuantityTrack(quantityTrack: Int) {
        quantityTracksStateLiveData.postValue(quantityTrack)
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistDbInteractor.deletaTrackInPlaylist(
                playlist, trackId
            )
            getPlaylist()
        }
    }

    fun clickOnShare(wordTrack: String) {
        if (playlist!!.quantityTracks == 0) {
            sharePlaylistStateLiveData.postValue(SharePlaylistStateInterface.playlistIsEmpity)
        } else {
            playlistInteractor.sharePlaylist(shareMessage(wordTrack))
        }
    }

    private fun shareMessage(wordTrack: String): String {
        val trackMessage = StringBuilder()
        trackInPlaylist.forEachIndexed { index, track ->
            trackMessage.append("${index + 1}. ${track.artistName} - ${track.trackName} ")
                .append("(${TimeUtils.formatTrackDuraction(track.trackTimeMillis.toInt())})\n")
        }

        return buildString {
            append(playlist?.playListName).append("\n")
            append(playlist?.playlistDescription).append("\n")
            append(wordTrack).append("\n")
            append(trackMessage)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistDbInteractor.deletePlayList(playlist!!)

        }
    }

    fun editPlaylist() {

    }
}