package com.example.chatflix

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val videoView:VideoView=findViewById(R.id.videoView)
        val videoPath:String="android.resource://"+packageName+"/"+R.raw.splash
        val uri:Uri=Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener
        {
            fun onPrepared(mp:MediaPlayer)
            {
                mp.start()
            }
        })
        videoView.setOnCompletionListener()
        {
            var intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }

       /* val mediaController:MediaController= MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)*/
        videoView.start()
    }
}