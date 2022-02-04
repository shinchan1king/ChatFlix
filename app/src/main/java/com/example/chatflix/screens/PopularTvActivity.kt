package com.example.chatflix.screens

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chatflix.adapters.PagedTvShowsAdapter
import com.example.chatflix.data.Injection
import com.example.chatflix.data.MediaViewModel
import com.example.chatflix.data_models.TvShow
import com.example.chatflix.databinding.ActivityPopularTvBinding
import com.example.chatflix.extensions.toMediaBsData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PopularTvActivity : BaseActivity() {
    private lateinit var binding: ActivityPopularTvBinding
    private lateinit var viewModel: MediaViewModel
    private lateinit var popularTvItemsAdapter: PagedTvShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPopularTvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()
    }

    private fun handleTvClick(tvShow: TvShow) {
        MediaDetailsBottomSheet.newInstance(tvShow.toMediaBsData())
            .show(supportFragmentManager, tvShow.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        popularTvItemsAdapter = PagedTvShowsAdapter(this::handleTvClick)
        binding.popularTvList.adapter = popularTvItemsAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideMediaViewModelFactory()
        ).get(MediaViewModel::class.java)
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                viewModel.getPopularTvShows().collectLatest {
                    popularTvItemsAdapter.submitData(it)
                }
            } catch (e: Exception) {
            }
        }
    }
}
