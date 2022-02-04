package com.example.chatflix.network.models

import com.example.chatflix.data_models.Episode
import com.squareup.moshi.Json

data class TvSeasonDetailsResponse(
    val id: Int,
    @Json(name = "season_number") val seasonNumber: Int,
    val name: String,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "air_date") val airDate: String?,
    val episodes: List<Episode>,
)