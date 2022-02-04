package com.example.chatflix.network.models

import com.example.chatflix.data_models.ITvShow
import com.example.chatflix.data_models.Movie
import com.example.chatflix.data_models.Season
import com.example.chatflix.data_models.TvShow
import com.squareup.moshi.Json

data class TvDetailsResponse(
        @Json(name = "id") override val id: Int,
        @Json(name = "name") override val name: String,
        @Json(name = "poster_path") override val posterPath: String?,
        @Json(name = "backdrop_path") override val backdropPath: String?,
        @Json(name = "overview") override val overview: String,
        @Json(name = "first_air_date") override val firstAirDate: String?,
        @Json(name = "vote_average") override val voteAverage: Double,
        @Json(name = "seasons") val seasons: List<Season>,
        @Json(name = "similar") val similar: PageResponse<TvShow>,
        @Json(name = "videos") val videos: VideosResponse,
) : ITvShow