package com.practicum.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTrackBinding
import com.practicum.playlistmaker.medialibrary.ui.models.FavoriteStateInterface
import com.practicum.playlistmaker.medialibrary.ui.view_model.FavoriteTrackViewModel
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.player.ui.fragment.PlayerFragment
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTrackFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTrackFragment()
    }

    private lateinit var favoriteTracksAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNothingWasFound: LinearLayout
    private lateinit var favoriteList: LinearLayout

    private val favoriteTrackViewModel: FavoriteTrackViewModel by viewModel()

    private var _binding: FragmentFavoriteTrackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        initAdapter()

        setListeners()

        favoriteTrackViewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

        favoriteTrackViewModel.showFavoriteTrack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initViews(){
        recyclerView = binding.recyclerViewFavorite
        placeholderNothingWasFound = binding.placeholderNothingWasFound
        favoriteList = binding.favoriteList
    }

    private fun render(state: FavoriteStateInterface){
        when (state){
            is FavoriteStateInterface.FavoriteTracksIsEmpty -> showPlaceHolder()
            is FavoriteStateInterface.FavoriteTracks -> showFavoriteTracks(state.favoriteTracks)
        }
    }

    private fun showPlaceHolder(){
        placeholderNothingWasFound.isVisible = true
        favoriteTracksAdapter.setTracks(null)
        favoriteList.isVisible = false
    }


    private fun showFavoriteTracks(tracks: List<Track>){
        favoriteTracksAdapter.setTracks(tracks)
        placeholderNothingWasFound.isVisible = false
        favoriteList.isVisible = true
    }

    private fun initAdapter() {
        favoriteTracksAdapter = TrackAdapter(ArrayList<Track>(), TrackAdapter.HIGH_RESOLUTION)
        recyclerView.adapter = favoriteTracksAdapter
    }

    private fun setListeners(){
        //Обработать нажатие на View трека в поиске
        favoriteTracksAdapter.itemClickListener = { position, track ->
            sendToPlayer(track)
        }
    }

    private fun sendToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            PlayerFragment.createArgs(track.trackId)
        )
    }
}
