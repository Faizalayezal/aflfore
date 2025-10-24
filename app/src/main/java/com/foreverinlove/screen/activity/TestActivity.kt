package com.foreverinlove.screen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foreverinlove.R
import com.foreverinlove.databinding.ActivityTestBinding
import com.foreverinlove.utility.stateProgress.StateProgressHelper


class TestActivity : AppCompatActivity() {
    private var binding : ActivityTestBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        StateProgressHelper
            .Builder()
            .setHorizontalMainView(binding!!.progress1)
            .setActiveColor(R.color.phaseColor1)
            .setMaxItems(5)
            .setSelected(3)
            .build()


    }




}

















