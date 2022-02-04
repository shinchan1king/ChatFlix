package com.example.chatflix.network.models

import com.example.chatflix.data_models.Video

data class VideosResponse(
        val results: List<Video>,
)