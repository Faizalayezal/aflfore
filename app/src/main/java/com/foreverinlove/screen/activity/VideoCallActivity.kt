package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityVideoCallBinding
import com.foreverinlove.singlevideocall.IncomingCallActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
class VideoCallActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoCallBinding
    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()

       // Glide.with(applicationContext).load(R.mipmap.img1).into(binding.imgBg)


        binding.imgBack.setOnClickListener {
            finish()
        }
         lifecycleScope.launch {
            delay(4000)
            startActivity(Intent(this@VideoCallActivity, IncomingCallActivity::class.java))
            finish()

        }


    }


}





