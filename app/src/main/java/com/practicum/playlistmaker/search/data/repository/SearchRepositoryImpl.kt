package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.db.data.AppDataBase
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.ResultLoadTracks
import com.practicum.playlistmaker.util.CheckTrackInFavorites
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDataBase: AppDataBase,
) : SearchRepository {
    override fun loadTracks(searchText: String): Flow<ResultLoadTracks> = flow {
        val resultLoadTracks = networkClient.loadTracks(searchText = searchText)
        val idFavoriteTracks = appDataBase.favoriteTrackDao().getIdFavoriteTrack()

        if (resultLoadTracks is ResultLoadTracks.OnSuccess) {
            resultLoadTracks.data =
                CheckTrackInFavorites.checkTrackInFavorites(resultLoadTracks.data, idFavoriteTracks)
        }
        emit(resultLoadTracks)
    }
}