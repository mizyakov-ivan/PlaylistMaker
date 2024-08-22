package com.practicum.playlistmaker.search.domain.models

sealed class NetworkError() {
    class ServerError() : NetworkError()

    class NoData() : NetworkError()

    class NoInternet() : NetworkError()
}