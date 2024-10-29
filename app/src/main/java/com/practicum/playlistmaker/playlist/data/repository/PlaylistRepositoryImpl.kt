package com.practicum.playlistmaker.playlist.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository

class PlaylistRepositoryImpl(private val gson: Gson) : PlaylistRepository {
    override fun getListIdTracks(idTracksInPlaylist: String): List<Int> {
        val typeTokenList = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(idTracksInPlaylist, typeTokenList) ?: emptyList()
    }
}