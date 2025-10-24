package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivitySettingProfileBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.SettingResponsedata
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class SettingProfileActivity : BaseActivity() {
    private var binding: ActivitySettingProfileBinding? = null
    private val viewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        screenOpened("MainSetting")
        binding?.suggestionBox?.imeOptions = EditorInfo.IME_ACTION_DONE
        binding?.suggestionBox?.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding?.imgBack?.setOnClickListener {
            onBackPressed()
        }
        binding?.suggestionBox?.addTextChangedListener {
           // binding?.txtcount?.text = "" + binding?.suggestionBox?.text.toString().length + "/100"
        }
        binding?.contact?.setOnClickListener {
            startActivity(Intent(this, ContactandSupportActivity::class.java))
        }
        binding?.faq?.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java).putExtra("type", "faq"))

        }
        binding?.privacyPolicy?.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java).putExtra("type", "pricy"))

        }
        binding?.termcondition?.setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java).putExtra("type", "term"))
        }
        binding?.notificaton?.setOnClickListener {
            startActivity(Intent(this, NotifitcationActivity::class.java))
        }

        binding?.suggestionBox?.movementMethod = null


        viewModel.start()

        lifecycleScope.launch {
            delay(500)
            viewModel.getSettings()

        }
        binding?.notificationSwitch?.setOnCheckedChangeListener { _, b ->
            val flag = if (b) 1 else 0
            lifecycleScope.launch {

                viewModel.updateSettingData(
                    SettingsViewModel.UpdateSettingType.UpdateShowNotification(
                        flag.toString()
                    )
                )
            }
        }

        binding?.emailSwitch?.setOnCheckedChangeListener { _, b ->
            val flag = if (b) 1 else 0
            lifecycleScope.launch {
                viewModel.updateSettingData(SettingsViewModel.UpdateSettingType.UpdateSendEmail(flag.toString()))
            }
        }

        binding?.toggle?.setOnCheckedChangeListener { _, i ->

            if (binding?.km?.id == i && !selectedDistance.equals("km", true)) {
                viewModel.updateSettingData(
                    SettingsViewModel.UpdateSettingType.UpdateDistanceVisible(
                        "0"
                    )
                )
            } else if (binding?.miles?.id == i && !selectedDistance.equals("miles", true)) {
                viewModel.updateSettingData(
                    SettingsViewModel.UpdateSettingType.UpdateDistanceVisible(
                        "1"
                    )
                )
            }
            selectedDistance = if (binding?.km?.id == i) "km" else "miles"
        }

        binding?.btnSubmit?.setOnClickListener {

            if (binding?.suggestionBox?.text.isNullOrEmpty()) {
                onBackPressed()
            } else {

                viewModel.callApiData(binding?.suggestionBox?.text.toString().trim())
            }

        }




        lifecycleScope.launch {

            viewModel.settingConversion.collect {
                when (it) {
                    SettingsViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is SettingsViewModel.GetLoginEvent.Failure -> {
                        showToast(it.errorText)

                        Utility.hideProgressBar()
                    }

                    SettingsViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is SettingsViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                            if (it.result.data != null) getData(it.result.data)
                        }


                    }
                }
            }

        }

        Listner()


    }

    private var selectedDistance = ""

    //allredy selected items
    private fun getData(data: SettingResponsedata) {
        binding?.notificationSwitch?.isChecked = data.show_notification == 1

        binding?.miles?.isChecked =
            data.distance_unit.equals("Mile", true) || data.distance_unit.equals("1")
        binding?.km?.isChecked =
            data.distance_unit.equals("Km", true) || data.distance_unit.equals("0")
        binding?.emailSwitch?.isChecked = data.send_mail == "1"
    }

    private fun Listner() {
        lifecycleScope.launch {
            viewModel.suggestionConversion.collect {
                when (it) {
                    SettingsViewModel.SuggestionEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is SettingsViewModel.SuggestionEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is SettingsViewModel.SuggestionEvent.Loading -> {
                        showProgressBar()

                    }

                    is SettingsViewModel.SuggestionEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                            showToast("Submit successfully")
                            onBackPressed()

                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
    }


}