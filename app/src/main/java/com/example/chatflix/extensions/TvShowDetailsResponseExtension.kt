package com.example.chatflix.extensions

import com.example.chatflix.data_models.Season
import com.example.chatflix.network.models.TvDetailsResponse

fun TvDetailsResponse.getInitialSeasonIndex(): Int {
    if (this.seasons.isNotEmpty()) {
        var initialSeasonIndex = this.seasons.indexOfFirst { it.seasonNumber > 0 }
        if (initialSeasonIndex == -1) {
            initialSeasonIndex = 0
        }
        return initialSeasonIndex;
    }
    return -1
}