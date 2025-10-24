package com.foreverinlove


import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityQaBinding


class QaActivity : BaseActivity() {

    private lateinit var binding: ActivityQaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("PhaseList")
        binding.apply {
            phase1.animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_transition1
            )
            phase2.animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_transition2
            )
            phase3.animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_transition3
            )
            phase4.animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_transition4
            )

        }

        binding.imgback.setOnClickListener {
           onBackPressed()
        }
        binding.phase1.setOnClickListener {
            startActivity(Intent(this@QaActivity, Phase1Activity::class.java))
            overridePendingTransition(
                R.anim.puse_up_in,
                R.anim.puse_up_out
            )
        }
        binding.phase2.setOnClickListener {
            startActivity(Intent(this@QaActivity, Phase2Activity::class.java))
            overridePendingTransition(
                R.anim.puse_up_in,
                R.anim.puse_up_out
            )
        }
        binding.phase3.setOnClickListener {
            startActivity(Intent(this@QaActivity, Phase3Activity::class.java))
            overridePendingTransition(
                R.anim.puse_up_in,
                R.anim.puse_up_out
            )
        }
        binding.phase4.setOnClickListener {
            startActivity(Intent(this@QaActivity, Phase4Activity::class.java))
            overridePendingTransition(
                R.anim.puse_up_in,
                R.anim.puse_up_out
            )
        }


    }


}