package com.example.chatflix.extensions

import com.example.chatflix.constants.IMAGE_BASE_URL
import com.example.chatflix.constants.ImageSize
import com.example.chatflix.data_models.Episode

fun Episode.getStillUrl(size: ImageSize = ImageSize.ORIGINAL): String {
    return "$IMAGE_BASE_URL${size.value}${this.stillPath}"
}
