package com.example.chatflix.data

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.chatflix.data_models.Movie
import com.example.chatflix.data_models.TvShow
import kotlinx.coroutines.flow.Flow

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    fun getUpcomingMovies(): Flow<PagingData<Movie>> {
        return repository.getUpcomingMovies()
    }

    fun getPopularMovies(): Flow<PagingData<Movie>> {
        return repository.getPopularMoviesStream()
    }

    fun getPopularTvShows(): Flow<PagingData<TvShow>> {
        return repository.getPopularTvShowsStream()
    }
}