package com.foreverinlove.screen.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.chatflow.PersonalChatActivity
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityNewMatchingAlgorithmBinding
import com.foreverinlove.network.response.SwipeData
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewMatchingAlgorithmActivity : BaseActivity() {
    private lateinit var binding: ActivityNewMatchingAlgorithmBinding

    private var tempUserDataObject: TempUserDataObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMatchingAlgorithmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("ProfileMatch")
        val imageid = intent.getSerializableExtra("matchData") as? SwipeData?
        Glide.with(applicationContext)
            .load(imageid?.match_user_image_url?.firstOrNull()?.url)
            .into(binding.border1)

        lifecycleScope.launch {
            this@NewMatchingAlgorithmActivity.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .firstOrNull {

                    tempUserDataObject = it
                    ImageLoad()


                    true
                }
        }


        Log.d("TAG", "onCasjgasjreate: "+imageid)


        binding.btnSayHii.setOnClickListener {
            val intent = Intent(applicationContext, PersonalChatActivity::class.java)
            intent.putExtra("currentUserId", tempUserDataObject?.id?.toIntOrNull()?:0)
            intent.putExtra("otherUserId", imageid?.matched_user_id  ?: 0)
            intent.putExtra("otherUserImage", imageid?.match_user_image_url?.firstOrNull()?.url ?: "")
            intent.putExtra("otherUserName", imageid?.match_user_name ?: "")
            intent.putExtra("matchId", imageid?.match_id ?: "")
            intent.putExtra("matchData2", imageid)
            intent.putExtra("matchMathi", "bhaibhai")
            startActivity(intent)
            finish()

        }

        binding.imgBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }
        binding.txtKeepSwiping.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }
    }

    private fun ImageLoad() {
        Glide.with(applicationContext).load(tempUserDataObject?.imageUrl1)
            .into(binding.border5)
        Glide.with(applicationContext).load(tempUserDataObject?.imageUrl2)
            .into(binding.border4)
        Glide.with(applicationContext).load(tempUserDataObject?.imageUrl3)
            .into(binding.border3)
        Glide.with(applicationContext).load(tempUserDataObject?.imageUrl1)
            .into(binding.border2)

    }


}
