package com.example.chatflix.extensions

import android.annotation.SuppressLint
import com.example.chatflix.constants.IMAGE_BASE_URL
import com.example.chatflix.constants.ImageSize
import com.example.chatflix.data_models.IMovie
import com.example.chatflix.data_models.ITvShow
import com.example.chatflix.data_models.MediaBsData
import java.text.SimpleDateFormat
import java.util.*

fun ITvShow.getPosterUrl(size: ImageSize = ImageSize.NORMAL): String {
    return "$IMAGE_BASE_URL${size.value}${this.posterPath}"
}

fun ITvShow.getBackdropUrl(size: ImageSize = ImageSize.ORIGINAL): String {
    return "$IMAGE_BASE_URL${size.value}${this.backdropPath}"
}

@SuppressLint("SimpleDateFormat")
fun ITvShow.getFirstAirDate(): String?{
    return if(this.firstAirDate == null){
        null
    }
    else{
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = format.parse(this.firstAirDate)
        val df = SimpleDateFormat("yyyy")
        val year = df.format(date)
        year
    }
}

fun ITvShow.toMediaBsData(): MediaBsData {
    return MediaBsData(
        "tv",
        this.id,
        this.getPosterUrl(),
        this.name,
        this.getFirstAirDate(),
        this.overview
    )
}