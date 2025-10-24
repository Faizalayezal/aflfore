package com.foreverinlove

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.foreverinlove.databinding.ActivityWatchBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class WatchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWatchBinding
   private var isFullScreen = false
   private var Lock = false
    private lateinit var bt_full:ImageView

    private var  simpleExoPlayer: SimpleExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bt_full = findViewById<ImageView>(R.id.bt_fullscreen)
        val bt_lock = findViewById<ImageView>(R.id.exo_lock)

        bt_full.setOnClickListener {
            if (!isFullScreen) {
                bt_full.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen_exit
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                bt_full.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            isFullScreen = !isFullScreen
        }

        bt_lock.setOnClickListener {
            if (!Lock) {
                bt_lock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock
                    )
                )
            } else {
                bt_lock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock_open
                    )
                )

            }
            Lock=!Lock
            bt_lock(Lock)
        }



        simpleExoPlayer = SimpleExoPlayer.Builder(this).setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000).build()
        binding.playerView.player = simpleExoPlayer
        binding.playerView.keepScreenOn = true

        simpleExoPlayer!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playbackState == Player.STATE_BUFFERING) {
                    binding.progressBar.visibility == View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {

                    binding.progressBar.visibility == View.GONE

                }


            }
        })



        val videoSource = intent.getStringExtra("videoplayer") ?: ""


        val mediaItem = MediaItem.fromUri(videoSource)
        Log.d("TAG", "oasasdsanCreate: "+mediaItem)
        Log.d("TAG", "oasasdsanCreate:1064 "+videoSource)
        simpleExoPlayer!!.setMediaItem(mediaItem)
        simpleExoPlayer!!.prepare()
        simpleExoPlayer!!.play()
        simpleExoPlayer!!.pause()
    }

    private fun bt_lock(lock: Boolean) {
           val sec_mid=findViewById<LinearLayout>(R.id.sec_controlvid1)
           val sec_bottom=findViewById<LinearLayout>(R.id.sec_controlvid2)

        if(lock){
            sec_mid.visibility=View.INVISIBLE
            sec_bottom.visibility=View.INVISIBLE
        }else{
            sec_mid.visibility=View.VISIBLE
            sec_bottom.visibility=View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer!!.pause()

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        if(Lock)
            return
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            bt_full.performClick()
        }
        else
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer!!.release()
    }
}