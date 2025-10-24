package com.foreverinlove.dynamicpopup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.databinding.ActivityDynamicpopupBinding

class DynamicpopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicpopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicpopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this@DynamicpopupActivity).load(R.mipmap.rocket).into(binding.imgPopUp)


    }
}