package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.databinding.ActivityBioScreenBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreClearAll
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import com.foreverinlove.viewmodels.BioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class BioScreenActivity : AppCompatActivity() {

    private var tempUserDataObject: TempUserDataObject? = null
    private val viewModel: BioViewModel by viewModels()

    private var binding: ActivityBioScreenBinding? = null
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBioScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel.start()
       // viewModel.callApiData()

        binding?.tellus?.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)


        binding?.btnnext?.setOnClickListener {

            if (isValidation()) {
                tempUserDataObject?.let { it1 -> viewModel.updateUserData(it1) }
                startActivity(Intent(this, AddProfilePictureActivity::class.java))

            }

        }

        binding?.imgBack?.setOnClickListener {
            runBlocking {
                dataStoreClearAll()
            }
            onBackPressed()
        }
        binding?.tellus?.addTextChangedListener {
            binding?.txtcount?.text = "" + binding?.tellus?.text.toString().length + "/100"
        }

        //data set mate
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                    if (tempUserDataObject?.about != "") {
                        binding?.tellus?.setText(it.about)
                    }

                }
        }


    }

    private fun isValidation(): Boolean {
        if (binding?.tellus?.text.toString().isEmpty()) {
            showToast("Please Enter Description")
            return false
        } else if (binding!!.tellus.text.length < 10) {
            showToast("Please Enter Minimum 10 Characters")
            return false
        }
        tempUserDataObject = TempUserDataObject(

            about = binding?.tellus?.text.toString().trim(),
        )

        return true

    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }

}

