package com.practicum.playlistmaker.player.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayerStateInterface
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackId: Int,
) : ViewModel() {

    init {
        playerInteractor.subscribeOnPlayer { state: PlayerState ->
            when (state) {
                PlayerState.STATE_PLAYING -> startPlayer()
                PlayerState.STATE_PREPARED -> preparePlayer()
                PlayerState.STATE_PAUSED -> pausePlayer()
                PlayerState.STATE_DEFAULT -> onScreenDestroyed()
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 300L
    }

    private var timerJob: Job? = null
    var playerState = PlayerState.STATE_DEFAULT

    private val playerStateLiveData = MutableLiveData<PlayerStateInterface>()
    private val timerLiveData = MutableLiveData<String>()
    private val trackHistoryStateLiveData = MutableLiveData<Track>()
    fun observeTrackHistoryState(): LiveData<Track> = trackHistoryStateLiveData
    fun observePlayerState(): LiveData<PlayerStateInterface> = playerStateLiveData
    fun observerTimerState(): LiveData<String> = timerLiveData

    override fun onCleared() {
        pausePlayer()
        onViewDestroyed()
        onScreenDestroyed()
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                playerInteractor.pausePlayer()
            }

            PlayerState.STATE_PREPARED,
            PlayerState.STATE_PAUSED,
            -> playerInteractor.startPlayer()

            PlayerState.STATE_DEFAULT -> defaultPlayer()
        }
    }

    fun activityPause() {
        if (playerState == PlayerState.STATE_PREPARED) return
        playerState = PlayerState.STATE_PAUSED
        playerInteractor.pausePlayer()
    }

    private fun startPlayer() {
        playerState = PlayerState.STATE_PLAYING
        playerStateLiveData.postValue(PlayerStateInterface.Play)
        startTimer()
    }

    private fun pausePlayer() {
        playerState = PlayerState.STATE_PAUSED
        playerStateLiveData.postValue(PlayerStateInterface.Pause)
        timerJob?.cancel()
    }


    fun startPreparePlayer(previewUrl: String?) {
        playerInteractor.preparePlayer(previewUrl)
        playerState = PlayerState.STATE_PREPARED
        playerStateLiveData.postValue(PlayerStateInterface.Prepare)
    }

    private fun defaultPlayer() {
        pausePlayer()
        playerState = PlayerState.STATE_DEFAULT
    }

    private fun preparePlayer() {
        playerState = PlayerState.STATE_PREPARED
        playerStateLiveData.postValue(PlayerStateInterface.Prepare)
        timerJob?.cancel()
    }

    private fun onScreenDestroyed() {
        playerInteractor.unSubscribeOnPlayer()
    }

    private fun onViewDestroyed() {
        playerInteractor.releasePlayer()
    }

    private fun getCurrentPosition(): String {
        return playerInteractor.getCurrentPosition()
    }

    fun getTrackHistory() {
        val sendTrack = playerInteractor.getTrackHistory().find { it.trackId == trackId } ?: return
        trackState(sendTrack)
    }

    private fun trackState(track: Track) {
        trackHistoryStateLiveData.postValue(track)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.STATE_PLAYING) {
                delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
                timerLiveData.value = getCurrentPosition()
            }
        }
    }
}