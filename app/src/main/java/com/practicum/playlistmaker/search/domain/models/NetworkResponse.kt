package com.practicum.playlistmaker.search.domain.models

sealed class NetworkResponse() {
    class SuccessRequest() : NetworkResponse()

    class ServerError() : NetworkResponse()
    class NoData() : NetworkResponse()

    class NoInternet() : NetworkResponse()
}