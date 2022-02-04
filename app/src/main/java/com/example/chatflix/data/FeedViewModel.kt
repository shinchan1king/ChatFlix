package com.example.chatflix.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.chatflix.data_models.FeedItem

class FeedViewModel : ViewModel() {
    var feedItemsSourceFactory: FeedItemsSourceFactory? = null

    fun getFeedPagedList(): LiveData<PagedList<FeedItem>> {
        feedItemsSourceFactory = FeedItemsSourceFactory(viewModelScope)
        return LivePagedListBuilder(feedItemsSourceFactory!!, getPagedListConfig()).build()
    }

    private fun getPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(5)
            .setPageSize(10)
            .build()
    }
}