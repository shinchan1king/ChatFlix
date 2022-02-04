package com.example.chatflix.screens

import adapters.VideosController
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.example.chatflix.adapters.MoviesAdapter

import com.example.chatflix.data.Injection
import com.example.chatflix.data.MovieDetailsViewModel
import com.example.chatflix.data_models.Movie
import com.example.chatflix.data_models.Video
import com.example.chatflix.databinding.ActivityMovieDetailsBinding
import com.example.chatflix.extensions.*
import com.example.chatflix.network.models.MovieDetailsResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MovieDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private lateinit var similarMoviesItemsAdapter: MoviesAdapter
    private lateinit var videosController: VideosController
    private val movieId: Int?
        get() = intent.extras?.getInt("id")
    var i=0
    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false
    var isEnded=true
    var coins2=-2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
       // binding.tabLayout.removeOnTabSelectedListener(tabSelectedListener)
    }

    private fun handleMovieClick(item: Movie) {
        MediaDetailsBottomSheet.newInstance(item.toMediaBsData())
            .show(supportFragmentManager, item.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.loader.root.show()
        binding.content.hide()
        binding.youtubePlayerView.hide()
        binding.thumbnail.container.hide()
        binding.thumbnail.playContainer.setOnClickListener { replayVideo() }

        binding.header.overviewText.setOnClickListener {
            binding.header.overviewText.maxLines = 10
            binding.header.overviewText.isClickable = false
        }

        binding.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
      // binding.tabLayout.addOnTabSelectedListener(tabSelectedListener)

        similarMoviesItemsAdapter = MoviesAdapter(this::handleMovieClick)
        binding.similarMoviesList.adapter = similarMoviesItemsAdapter
        binding.similarMoviesList.isNestedScrollingEnabled = false

        videosController = VideosController {}
        binding.videosList.adapter = videosController.adapter
        binding.videosList.isNestedScrollingEnabled = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViewModel() {
        movieDetailsViewModel = ViewModelProvider(this,
            Injection.provideMovieDetailsViewModelFactory()).get(MovieDetailsViewModel::class.java)

        movieDetailsViewModel.details.observe(this) {
            if (it.isLoading && it.data == null) {
                showLoader(true)
            } else if (it.data != null) {
                showLoader(false)
                updateDetails(it.data)
            }
        }
    }

    private fun fetchData() {
        if (movieId != null) {
            movieDetailsViewModel.fetchMovieDetails(movieId!!)
        }
    }

    private fun showLoader(flag: Boolean) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDetails(details: MovieDetailsResponse) {
        // Basic details
        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
            .into(binding.thumbnail.backdropImage)
        binding.header.titleText.text = details.title
        binding.header.overviewText.text = details.overview
        binding.header.yearText.text = details.getReleaseYear()
        binding.header.runtimeText.text = details.getRunTime()
        binding.header.ratingText.text = details.voteAverage.toString()

        // Similar movies
        similarMoviesItemsAdapter.submitList(details.similar.results)
        similarMoviesItemsAdapter.notifyDataSetChanged()

        // Videos
        checkAndLoadVideo(details.videos.results)
        videosController.setData(details.videos.results)
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

    private fun replayVideo() {
        if (player != null) {
            player!!.seekTo(0f)
            lifecycleScope.launch {
                delay(500)
                binding.youtubePlayerView.show()
                binding.thumbnail.container.hide()
            }
        }
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
            if ((state == PlayerConstants.PlayerState.UNSTARTED) && !isVideoRestarted) {
                youTubePlayer.mute()
                if (state == PlayerConstants.PlayerState.ENDED) {
                    binding.youtubePlayerView.hide()
                    binding.thumbnail.container.show()
                    binding.thumbnail.videoLoader.hide()

                }
            }
        }

        private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.similarMoviesList.show()
                    binding.videosList.hide()
                } else {
                    binding.similarMoviesList.hide()
                    binding.videosList.show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
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
