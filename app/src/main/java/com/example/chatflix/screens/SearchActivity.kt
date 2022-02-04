package com.example.chatflix.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.chatflix.adapters.MediaItemsAdapter
import com.example.chatflix.data.Injection
import com.example.chatflix.data.SearchResultsViewModel
import com.example.chatflix.data_models.Media
import com.example.chatflix.data_models.Movie
import com.example.chatflix.databinding.ActivitySearchBinding
import com.example.chatflix.extensions.hide
import com.example.chatflix.extensions.hideKeyboard
import com.example.chatflix.extensions.show
import com.example.chatflix.adapters.TopMoviesController
import com.example.chatflix.extensions.toMediaBsData


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchResultsViewModel: SearchResultsViewModel
    private lateinit var topSearchesController: TopMoviesController
    private lateinit var searchResultsAdapter: MediaItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.clearSearchIcon.setOnClickListener {
            binding.searchTextInput.setText("")
        }
        binding.searchTextInput.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                searchResultsViewModel.fetchSearchResults(query)
            }
            updateUI()
        }

        topSearchesController = TopMoviesController(this::handleMovieClick)
        binding.topSearchesList.adapter = topSearchesController.adapter
        binding.topSearchesList.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchTextInput.clearFocus()
            false
        }

        binding.resultsList.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchTextInput.clearFocus()
            false
        }

        searchResultsAdapter = MediaItemsAdapter(this::handleMediaClick)
        binding.resultsList.adapter = searchResultsAdapter
    }

    private fun handleMovieClick(movie: Movie) {
        hideKeyboard()
        binding.searchTextInput.clearFocus()
        MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
            .show(supportFragmentManager, movie.id.toString())
    }

    private fun handleMediaClick(media: Media) {
        hideKeyboard()
        binding.searchTextInput.clearFocus()
        var id: Int? = null
        if (media is Media.Movie) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(supportFragmentManager, id.toString())
        } else if (media is Media.Tv) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(supportFragmentManager, id.toString())
        }
    }

    private fun setupViewModel() {
        searchResultsViewModel =
            ViewModelProvider(this, Injection.provideSearchResultsViewModelFactory()).get(
                SearchResultsViewModel::class.java
            )

        searchResultsViewModel.popularMoviesLoading.observe(this) { }
        searchResultsViewModel.popularMovies.observe(this) {
            if (it != null) {
                topSearchesController.setData(it)
            }
        }
        searchResultsViewModel.searchResultsLoading.observe(this) { loading ->
            val searchResults = searchResultsViewModel.searchResults.value
            if (loading && searchResults == null) {
                binding.searchResultsLoader.show()
            } else {
                binding.searchResultsLoader.hide()
            }
        }
        searchResultsViewModel.searchResults.observe(this) {
            searchResultsAdapter.submitList(it)
        }
    }

    private fun updateUI() {
        val query = binding.searchTextInput.text.trim().toString()
        if (query.isEmpty()) {
            binding.emptySearchContent.show()
            binding.searchResultsContent.hide()
        } else {
            val searchResultsLoading = searchResultsViewModel.searchResultsLoading.value!!
            val searchResults = searchResultsViewModel.searchResults.value
            binding.emptySearchContent.hide()
            binding.searchResultsContent.show()

            if (searchResultsLoading && searchResults == null) {
                binding.searchResultsLoader.show()
            } else {
                binding.searchResultsLoader.hide()
            }
        }
    }


    private fun fetchData() {
        searchResultsViewModel.fetchPopularMovies()
    }
}