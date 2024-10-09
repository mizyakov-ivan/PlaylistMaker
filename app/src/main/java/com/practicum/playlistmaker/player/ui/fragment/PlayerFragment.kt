package com.practicum.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.TimeUtils
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.models.LikeStateInterface
import com.practicum.playlistmaker.player.ui.models.PlayerStateInterface
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

//Переменные
    lateinit var buttonArrowBackSearch: androidx.appcompat.widget.Toolbar
    lateinit var track: Track
    lateinit var artworkUrl100: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackTime: TextView
    lateinit var collectionName: TextView
    lateinit var releaseDate: TextView
    lateinit var primaryGenreName: TextView
    lateinit var country: TextView
    lateinit var duration: TextView
    lateinit var buttonPlay: ImageView
    lateinit var buttonLike: ImageView

    companion object {
        private const val SEND_TRACK_ID = "send_track_id"
        fun createArgs(sendTrackId: Int): Bundle {
            return bundleOf(SEND_TRACK_ID to sendTrackId)
        }
    }

    private val playerViewModel: PlayerViewModel by viewModel {
    parametersOf(requireArguments().getInt(SEND_TRACK_ID))
    }

    private lateinit var binding: FragmentPlayerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()

        playerViewModel.observeTrackState().observe(viewLifecycleOwner) { track ->
            getInfoTrack(track)
        }

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
        }

        playerViewModel.observerTimerState().observe(viewLifecycleOwner) { time ->
            duration.text = time
        }

        playerViewModel.observeIsFavoriteState().observe(viewLifecycleOwner) {
            renderLike(it)
        }

        setListeners()

    }
    override fun onPause() {
        super.onPause()
        playerViewModel.activityPause()

    }
    override fun onDestroy() {
        super.onDestroy()
    }

    fun getInfoTrack(track: Track) {
    showDataTrack(track)
    }

    private fun render(state: PlayerStateInterface) {
    when (state) {
            is PlayerStateInterface.Play -> play()
            is PlayerStateInterface.Pause -> pause()
            is PlayerStateInterface.Prepare -> prepare()
        }
    }
    private fun renderLike(state: LikeStateInterface) {
        when (state) {
            is LikeStateInterface.LikeTrack ->
                buttonLike.setImageResource(R.drawable.ic_button_like_on)

            is LikeStateInterface.NotLikeTrack ->
                buttonLike.setImageResource(R.drawable.ic_button_like_off)
        }
    }

    private fun prepare(){
        buttonPlay.isEnabled = true
        buttonPlay.setImageResource(R.drawable.play_button)
        duration.text = getString(R.string.duration_start)

    }

    private fun play(){
        buttonPlay.setImageResource(R.drawable.pause_button)
    }

    private fun pause(){
        buttonPlay.isEnabled = true
        buttonPlay.setImageResource(R.drawable.play_button)
    }

    private fun initViews() {
        buttonArrowBackSearch = binding.toolbarSetting
        artworkUrl100 = binding.artworkUrl100
        trackName = binding.trackName
        artistName = binding.artistName
        trackTime = binding.lengthD
        collectionName = binding.collectionNameV
        releaseDate = binding.yearV
        primaryGenreName = binding.genreV
        country = binding.countryV
        duration = binding.duration
        buttonPlay = binding.playButton
        buttonLike = binding.likeTrack
    }
    //Настроить Listeners
    private fun setListeners() {
        //Обработка нажатия на ToolBar "<-" и переход
        // на главный экран через закрытие экрана "Настройки"
        buttonArrowBackSearch.setOnClickListener() {
            findNavController().navigateUp()
        }

        buttonPlay.setOnClickListener() {
            playerViewModel.playbackControl()
        }

        buttonLike.setOnClickListener() {
            playerViewModel.onFavoriteClicked()
        }
    }

    private fun showDataTrack(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = TimeUtils.formatTrackDuraction(track.trackTimeMillis.toInt())
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate.substring(0..3)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        duration.text = getString(R.string.duration_start)
        val roundingRadius = this.resources.getDimensionPixelSize(R.dimen.s_padding)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(artworkUrl100)
    }
}

