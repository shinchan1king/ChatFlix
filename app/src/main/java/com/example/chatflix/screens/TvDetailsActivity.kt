package com.example.chatflix.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.example.chatflix.adapters.EpisodeItemsAdapter
import com.example.chatflix.adapters.TvShowsAdapter
import adapters.VideosController
import android.util.Log
import com.example.chatflix.data.Injection
import com.example.chatflix.data.TvShowDetailsViewModel
import com.example.chatflix.data_models.Resource
import com.example.chatflix.data_models.TvShow
import com.example.chatflix.data_models.Video
import com.example.chatflix.databinding.ActivityTvDetailsScreenBinding
import com.example.chatflix.extensions.*
import com.example.chatflix.network.models.TvDetailsResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TvDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityTvDetailsScreenBinding
    private lateinit var tvShowDetailsViewModel: TvShowDetailsViewModel
    private lateinit var episodeItemsAdapter: EpisodeItemsAdapter
    private lateinit var similarTvItemsAdapter: TvShowsAdapter
    private lateinit var videosController: VideosController
    private val tvId: Int?
        get() = intent.extras?.getInt("id")


    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false
    var coins2=-2000
    var isEnded=true
    var i=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchInitialData()

    }


    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
    }

    private fun handleTvClick(tvShow: TvShow) {
        MediaDetailsBottomSheet.newInstance(tvShow.toMediaBsData())
            .show(supportFragmentManager, tvShow.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        title = ""
        showBackIcon()

        binding.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        binding.header.overviewText.setOnClickListener {
            binding.header.overviewText.maxLines = 10
            binding.header.overviewText.isClickable = false
        }
        binding.menusTabLayout.addOnTabSelectedListener(tabSelectedListener)

        binding.seasonPicker.setOnClickListener { handleSeasonPickerSelectClick() }

        episodeItemsAdapter = EpisodeItemsAdapter {}
        binding.episodesList.adapter = episodeItemsAdapter
        binding.episodesList.isNestedScrollingEnabled = false

        similarTvItemsAdapter = TvShowsAdapter(this::handleTvClick)
        binding.similarTvsList.adapter = similarTvItemsAdapter
        binding.similarTvsList.isNestedScrollingEnabled = false

        videosController = VideosController {}
        binding.videosList.adapter = videosController.adapter
        binding.videosList.isNestedScrollingEnabled = false
    }

    private fun handleSeasonPickerSelectClick() {
        val details = tvShowDetailsViewModel.details.value?.data
        if (details != null) {
            val seasonNames =
                details.seasons.mapIndexed { _, season -> season.name } as ArrayList<String>

            val itemPickerFragment: ItemPickerFragment =
                ItemPickerFragment.newInstance(seasonNames,
                    tvShowDetailsViewModel.selectedSeasonNameIndexPair.value?.second!!)
            itemPickerFragment.showsDialog = true
            itemPickerFragment.show(supportFragmentManager, "pickerDialog")
            itemPickerFragment.setItemClickListener { newSelectedPosition ->
                val selectedSeason = details.seasons[newSelectedPosition]
                tvShowDetailsViewModel.selectedSeasonNameIndexPair.value =
                    Pair(selectedSeason.name, newSelectedPosition)
                lifecycleScope.launch {
                    tvShowDetailsViewModel.fetchSeasonDetails(tvId!!, selectedSeason.seasonNumber)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViewModel() {
        tvShowDetailsViewModel = ViewModelProvider(this,
            Injection.provideTvShowDetailsViewModelFactory()).get(TvShowDetailsViewModel::class.java)

        tvShowDetailsViewModel.details.observe(this) {
            val loading = (it!!.isLoading && it.data == null)
            if (loading) {
                setLoading(true)
            } else if (it.data != null) {
                setLoading(false)
                updateDetails(it.data)

                // Similar TV Shows
                similarTvItemsAdapter.submitList(it.data.similar.results)
                similarTvItemsAdapter.notifyDataSetChanged()

                // Videos
                checkAndLoadVideo(it.data.videos.results)
                videosController.setData(it.data.videos.results)
            }
        }

        tvShowDetailsViewModel.selectedSeasonNameIndexPair.observe(this) {
            if (it != null) {
                binding.selectedSeasonText.text = it.first
            }
        }

        tvShowDetailsViewModel.selectedSeasonDetails.observe(this) {
            if (it.data != null) {
                episodeItemsAdapter.submitList(it.data.episodes)
            }
        }
    }

    private fun setLoading(flag: Boolean) {
        if (flag) {
            binding.loader.root.show()
            binding.content.hide()
            binding.youtubePlayerView.hide()
            binding.thumbnail.container.hide()
        } else {
            binding.loader.root.hide()
            binding.content.show()
            binding.thumbnail.container.show()
        }
    }

    private fun fetchInitialData() {
        if (tvId != null) {
            tvShowDetailsViewModel.fetchTvShowDetails(tvId!!)
        }
    }

    private fun updateDetails(details: TvDetailsResponse) {
        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
            .into(binding.thumbnail.backdropImage)
        binding.header.titleText.text = details.name
        binding.header.overviewText.text = details.overview
        binding.header.yearText.text = details.getFirstAirDate()
        binding.header.runtimeText.visibility = View.GONE
        binding.header.ratingText.text = details.voteAverage.toString()
    }
    
    private fun checkAndLoadVideo(videos: List<Video>) {
        val firstVideo =
            videos.firstOrNull { video -> (video.type == "Trailer") && video.site == "YouTube" }
        if (firstVideo != null) {
            if (!bannerVideoLoaded) {
                binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        player = youTubePlayer
                        youTubePlayer.loadVideo(firstVideo.key, 0f)
                        bannerVideoLoaded = true
                    }
                })
            }
        } else {
            binding.thumbnail.playContainer.hide()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            if (!isVideoRestarted && second > 3.2) {
                isVideoRestarted = true
                lifecycleScope.launch {
                    youTubePlayer.seekTo(0f)
                    youTubePlayer.unMute()
                    binding.youtubePlayerView.getPlayerUiController().showUi(false)
                    delay(50)
                    binding.thumbnail.container.hide()
                    binding.thumbnail.videoLoader.hide()
                    binding.youtubePlayerView.show()
                    delay(1000)
                    binding.youtubePlayerView.getPlayerUiController().showUi(true)
                }
            }
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState,
        ) {
            if (!isVideoRestarted) {
                youTubePlayer.mute()

            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                binding.youtubePlayerView.hide()
                binding.thumbnail.container.show()
                binding.thumbnail.videoLoader.hide()
            }
        }
    }

    private val tabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    binding.seasonPicker.show()
                    binding.similarTvsList.hide()
                    binding.episodesList.show()
                    binding.videosList.hide()
                    binding.tabContentLoader.hide()
                }
                1 -> {
                    binding.seasonPicker.hide()
                    binding.episodesList.hide()
                    binding.similarTvsList.show()
                    binding.videosList.hide()
                }
                2 -> {
                    binding.seasonPicker.hide()
                    binding.similarTvsList.hide()
                    binding.episodesList.hide()
                    binding.videosList.show()
                    binding.tabContentLoader.hide()
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
    fun readData(Rslt: Query)
    {


        Rslt.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    for (data in snapshot.children) {


                        var coins1 = data.getValue().toString()

                        var coins = coins1.toInt() + 1000
                        if(coins1.toInt()==coins2+1000)
                        {


                        }
                        else {
                            val user: HashMap<String, Int> = hashMapOf(
                                "coin" to coins)
                            var database = FirebaseDatabase.getInstance()
                            val myRef = database.reference
                            val mAuth = FirebaseAuth.getInstance()
                            mAuth.currentUser?.let {
                                myRef.child("users").child(it.uid).setValue(user)
                            }

                        }
                        coins2 = coins1.toInt()
                    }

                }
            }
        })


    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", coins2.toString())
        val database = FirebaseDatabase.getInstance()
        val coins = 0;
        val user: HashMap<String, Int> = hashMapOf(
            "coin" to coins)
        val myRef = database.reference
        val mAuth = FirebaseAuth.getInstance()
        val myRslt = mAuth.currentUser?.let { myRef.child("users").child(it.uid) }
        if (myRslt != null&&isEnded&&i==0) {
            i=i+1;
            readData(myRslt)
            isEnded=false
        }

    }

    override fun onRestart() {
        super.onRestart()
        Log.d("TAG", coins2.toString())
        val database = FirebaseDatabase.getInstance()
        val coins = 0;
        val user: HashMap<String, Int> = hashMapOf(
            "coin" to coins)
        val myRef = database.reference
        val mAuth = FirebaseAuth.getInstance()
        val myRslt = mAuth.currentUser?.let { myRef.child("users").child(it.uid) }
        if (myRslt != null&&isEnded&&i==0) {
            i=i+1;
            readData(myRslt)
            isEnded=false
        }
    }
}