package com.example.chatflix.screens

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chatflix.adapters.PagedMoviesAdapter
import com.example.chatflix.data.Injection
import com.example.chatflix.data.MediaViewModel
import com.example.chatflix.data_models.Movie
import com.example.chatflix.databinding.ActivityPopularMoviesBinding
import com.example.chatflix.extensions.toMediaBsData
import kotlinx.coroutines.flow.collectLatest


class PopularMoviesActivity : BaseActivity() {
    private lateinit var binding: ActivityPopularMoviesBinding
    private lateinit var viewModel: MediaViewModel
    private lateinit var popularMoviesItemsAdapter: PagedMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()
    }

    private fun handleMovieClick(movie: Movie) {
        MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
            .show(supportFragmentManager, movie.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        popularMoviesItemsAdapter = PagedMoviesAdapter(this::handleMovieClick)
        binding.popularMoviesList.adapter = popularMoviesItemsAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideMediaViewModelFactory()
        ).get(MediaViewModel::class.java)
    }

    private fun fetchData() {
        lifecycleScope.launchWhenCreated {
            try {
                viewModel.getPopularMovies().collectLatest {
                    popularMoviesItemsAdapter.submitData(it)
                }
            } catch (e: Exception) {
            }
        }
    }
}