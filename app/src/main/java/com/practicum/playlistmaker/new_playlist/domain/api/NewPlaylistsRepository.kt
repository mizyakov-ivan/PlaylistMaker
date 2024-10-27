package com.practicum.playlistmaker.new_playlist.domain.api

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface NewPlaylistsRepository {

    suspend fun saveImageToPrivateStorage(uri: Uri, albumName: String): Flow<Uri>

}