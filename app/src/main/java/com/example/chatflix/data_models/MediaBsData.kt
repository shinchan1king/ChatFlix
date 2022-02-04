package com.example.chatflix.data_models

data class MediaBsData(
        val mediaType: String,
        val mediaId: Int,
        val posterUrl: String?,
        val title: String,
        val releaseYear: String?,
        val overview: String,
)