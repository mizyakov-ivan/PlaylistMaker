package com.practicum.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.TimeUtils
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.medialibrary.ui.models.PlaylistsStateInterface
import com.practicum.playlistmaker.newplaylist.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.adapter.PlaylistAdapterBottomSheet
import com.practicum.playlistmaker.player.ui.models.LikeStateInterface
import com.practicum.playlistmaker.player.ui.models.PlayerStateInterface
import com.practicum.playlistmaker.player.ui.models.TrackInPlaylistStateInterface
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

//Переменные
    private lateinit var buttonArrowBackSearch: androidx.appcompat.widget.Toolbar
    private lateinit var track: Track
    private lateinit var artworkUrl100: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var duration: TextView
    private lateinit var buttonPlay: ImageView
    private lateinit var buttonLike: ImageView
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var buttonAddTrack: FloatingActionButton
    private lateinit var recyclerViewBottomSheet: RecyclerView
    private lateinit var buttonNewPlaylist: Button
    private lateinit var overlay: View

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
    private lateinit var playlistAdapterBottomSheet: PlaylistAdapterBottomSheet
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        bottomSheetBehavior.peekHeight = (screenHeight * 2 / 3)

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

        playerViewModel.observePlaylistState().observe(viewLifecycleOwner){track ->
            renderPlaylists(track)
        }

        playerViewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner){
                state -> renderAddTrackToPlaylist(state)
        }

        initAdapter()

        setListeners()

    }
    private fun renderAddTrackToPlaylist(state: TrackInPlaylistStateInterface?) {
        when(state){
            is TrackInPlaylistStateInterface.TrackOnPlaylist ->{
                showToast(getString(R.string.trackOnPlaylist) + " " + state.namePlaylist)
            }
            is TrackInPlaylistStateInterface.TrackAddToPlaylist ->{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showToast(getString(R.string.trackAddOnPlaylist) + " " + state.namePlaylist)}
        }
    }

    private fun renderPlaylists(state: PlaylistsStateInterface) {
        when (state){
            is PlaylistsStateInterface.PlaylistsIsEmpty -> return
            is PlaylistsStateInterface.Playlists -> showPlaylists(state.playlists)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.activityPause()
        playerViewModel.eraseState()

    }
    override fun onDestroy() {
        super.onDestroy()
    }

    fun getInfoTrack(track: Track) {
    showDataTrack(track)
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistAdapterBottomSheet.setPlaylists(playlists)
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
        trackTime = binding.trackTimeData
        collectionName = binding.collectionNameData
        releaseDate = binding.releaseDateData
        primaryGenreName = binding.primaryGenreNameData
        country = binding.countryData
        duration = binding.duration
        buttonPlay = binding.playTrack
        buttonLike = binding.likeTrack
        buttonAddTrack = binding.buttonAddTrack
        bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        recyclerViewBottomSheet = binding.recyclerPlaylist
        buttonNewPlaylist = binding.buttonNewPlaylist
        overlay = binding.overlay
    }

    private fun initAdapter() {
        playlistAdapterBottomSheet = PlaylistAdapterBottomSheet(ArrayList<Playlist>())
        recyclerViewBottomSheet.adapter = playlistAdapterBottomSheet
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

        buttonAddTrack.setOnClickListener(){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        playerViewModel.showPlaylist()
                        overlay.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //overlay.alpha = slideOffset
            }
        })

        buttonNewPlaylist.setOnClickListener(){
            sendToNewPlaylist()
        }

        playlistAdapterBottomSheet.itemClickListener = {position, tracks, playlist ->
            playerViewModel.onPlaylistClick(tracks, playlist)
        }
    }

    private fun sendToNewPlaylist() {
        findNavController().navigate(
            R.id.action_playerFragment_to_newPlaylistFragment
        )
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

    private fun showToast(messageToast: String) {
        val toast = Toast.makeText(requireContext(), messageToast, Toast.LENGTH_LONG)
        toast.show()
    }
}

