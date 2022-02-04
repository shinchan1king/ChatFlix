package com.example.chatflix.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatflix.data_models.Resource
import com.example.chatflix.network.models.MovieDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val repository: MediaRepository) : ViewModel() {
    val details: MutableLiveData<Resource<MovieDetailsResponse>> =
        MutableLiveData(Resource(false, null, null))

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            details.postValue(details.value!!.copy(isLoading = true))
            try {
                val response = repository.fetchMovieDetails(id)
                details.postValue(details.value!!.copy(isLoading = false, data = response))
            } catch (e: Exception) {
                details.postValue(details.value!!.copy(isLoading = false, error = e.message))
            }
        }
    }
}