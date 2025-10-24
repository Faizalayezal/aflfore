package com.foreverinlove

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.databinding.ActivityComplatedRegisterBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.screen.activity.MainActivity
import com.foreverinlove.utility.dataStoreSetGroupPopup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ComplatedRegisterActivity : AppCompatActivity() {
    private var binding: ActivityComplatedRegisterBinding? = null
    private lateinit var firabseauth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplatedRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firabseauth= FirebaseAuth.getInstance()


        Glide.with(this@ComplatedRegisterActivity).load(R.mipmap.rgsucess)
            .into(binding!!.imageView3)
        binding?.adtional?.setOnClickListener {
            lifecycleScope.launch {
                this@ComplatedRegisterActivity.dataStoreSetGroupPopup("yes")
            }

            startActivity(Intent(this@ComplatedRegisterActivity, Phase1Activity::class.java))
        }

        anonymusAuth()


        binding?.btnContinue?.setOnClickListener {
            lifecycleScope.launch {
                this@ComplatedRegisterActivity.dataStoreSetGroupPopup("yes")

                startActivity(Intent(this@ComplatedRegisterActivity, MainActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }

    private fun anonymusAuth() {
        firabseauth.signInAnonymously().addOnSuccessListener {
            Log.d("TAG", "anonymusAuth: "+it.user?.uid)

        }.addOnCanceledListener {
            Log.d("TAG", "anonymusAuth: "+"sjdgsdfjhg")

        }

    }
}