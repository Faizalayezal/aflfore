package com.foreverinlove.screen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foreverinlove.R
import com.foreverinlove.utility.InternetConnectivityHelper

class NoInternetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        InternetConnectivityHelper.checkConnection(applicationContext) {
            if (it) finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}