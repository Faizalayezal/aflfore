package com.foreverinlove

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.databinding.ActivityEmailOtpBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.TextViewExt
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.CreateProfileViewModel
import com.foreverinlove.viewmodels.EditprofileSetDataViewModel
import com.foreverinlove.viewmodels.EmailOtpViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailOtpActivity : AppCompatActivity() {
    private var binding: ActivityEmailOtpBinding? = null
    private val viewModelOtp: EmailOtpViewModel by viewModels()
    private val viewModel: CreateProfileViewModel by viewModels()
    private val viewModelupdate: EditprofileSetDataViewModel by viewModels()
    private var tempUserDataObject: TempUserDataObject? = null

    var currentEmailText: String? = null
    var fromEdit: String? = null
    var emailInValid: Boolean? = false
    var emailChangeObserve: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailOtpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        TextViewExt.setOtpFlow(binding!!.otp1, binding!!.otp2, binding!!.otp3, binding!!.otp4)
        fromEdit = intent.getStringExtra("IsForm") ?: ""
        val tempEmailNew = intent.getStringExtra("IsFormEmail") ?: ""

        viewModelOtp.start()
        viewModel.start()
        viewModelupdate.start()



        lifecycleScope.launch {
            this@EmailOtpActivity.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    Log.d("TAG", "sfdsdfdfonCreate: "+it.imageUrl1)
                    Log.d("TAG", "sfdsdfdfonCreate:68 "+it.emailVerified)
                    tempUserDataObject = it

                }
        }

        viewModelOtp.tempEmailUserDataObject.observe(this) {
            if (!emailChangeObserve) return@observe

            if (fromEdit == "EditProfileFlow") {
                binding?.txtemail?.text = tempEmailNew
                binding?.pencil?.visibility = View.GONE

                if (tempUserDataObject?.emailVerified == "0") {
                    tempUserDataObject?.emailVerified = "1"
                    Log.d("TAG", "onsssCreate: " + tempUserDataObject?.emailVerified)
                }


            } else if (fromEdit == "RegisterFlow") {
                binding?.txtemail?.text = tempUserDataObject?.email
            }
        }


        Glide.with(this@EmailOtpActivity).load(R.mipmap.email).into(binding!!.imageView3)
        binding?.btnContinue?.setOnClickListener {
            if (dataValid()) {
                if (fromEdit == "RegisterFlow") {
                    Log.d("TAG", "onCreate333: " + "rgisterflow")

                    viewModelOtp.getOtpStatus(
                        tempOtp,
                        binding?.txtemail?.text.toString()
                    ){
                        if(it==1){
                        }
                    }

                } else {
                    viewModelOtp.getProfileOtpStatus(
                        tempOtp,
                        binding?.txtemail?.text.toString()
                    )
                }

            }
        }



        if (fromEdit == "RegisterFlow") {

            lifecycleScope.launch {

                viewModelOtp.otpConversion.collect {
                    when (it) {
                        EmailOtpViewModel.GetOtpEvent.Empty -> {
                            Utility.hideProgressBar()
                        }

                        is EmailOtpViewModel.GetOtpEvent.Failure -> {
                            showToast(it.errorText)

                            Utility.hideProgressBar()
                        }

                        EmailOtpViewModel.GetOtpEvent.Loading -> {
                            showProgressBar()
                        }

                        is EmailOtpViewModel.GetOtpEvent.Success -> {
                            Utility.hideProgressBar()

                            /* startActivity(
                                Intent(
                                    this@EmailOtpActivity,
                                    IdVerifyActivity::class.java
                                )
                            )*/


                            startActivity(
                                Intent(
                                    this@EmailOtpActivity,
                                    ComplatedRegisterActivity::class.java
                                )
                            )

                        }
                    }
                }

            }
            lifecycleScope.launch {
                viewModelOtp.resendEMAILPhoneOtpConversion.collect {
                    when (it) {
                        EmailOtpViewModel.ResendEmailOtpEvent.Empty -> {
                            Utility.hideProgressBar()
                        }

                        is EmailOtpViewModel.ResendEmailOtpEvent.Failure -> {
                            showToast(it.errorText)

                            Utility.hideProgressBar()
                        }

                        EmailOtpViewModel.ResendEmailOtpEvent.Loading -> {
                            showProgressBar()
                        }

                        is EmailOtpViewModel.ResendEmailOtpEvent.Success -> {
                            Utility.hideProgressBar()
                            // showToast(it.result.data)
                            showToast("OTP Resent Successfully")

                        }
                    }
                }
            }

        } else {

            lifecycleScope.launch {

                viewModelOtp.profileOtpConversion.collect {
                    when (it) {
                        EmailOtpViewModel.GetProfileOtpEvent.Empty -> {
                            Utility.hideProgressBar()
                        }

                        is EmailOtpViewModel.GetProfileOtpEvent.Failure -> {
                            showToast(it.errorText)

                            Utility.hideProgressBar()
                        }

                        EmailOtpViewModel.GetProfileOtpEvent.Loading -> {
                            showProgressBar()
                        }

                        is EmailOtpViewModel.GetProfileOtpEvent.Success -> {
                            Utility.hideProgressBar()

                            finish()
                        }
                    }
                }

            }

            lifecycleScope.launch {
                viewModelOtp.profileResendEMAILPhoneOtpConversion.collect {
                    when (it) {
                        EmailOtpViewModel.ResendProfileEmailOtpEvent.Empty -> {
                            Utility.hideProgressBar()
                        }

                        is EmailOtpViewModel.ResendProfileEmailOtpEvent.Failure -> {
                            showToast(it.errorText)

                            Utility.hideProgressBar()
                        }

                        EmailOtpViewModel.ResendProfileEmailOtpEvent.Loading -> {
                            showProgressBar()
                        }

                        is EmailOtpViewModel.ResendProfileEmailOtpEvent.Success -> {
                            Utility.hideProgressBar()
                            // showToast(it.result.data)
                            showToast("Otp Resend successfully")

                        }
                    }
                }
            }

        }







        binding?.txtResend?.setOnClickListener {
            if (fromEdit == "RegisterFlow") {
                Log.d("TAG", "onCreate11: " + "rgisterflow")

                viewModelOtp.getResendOtp(binding?.txtemail?.text.toString())
            } else {
                viewModelOtp.getProfileResendOtp(binding?.txtemail?.text.toString())

            }
        }
        binding?.pencil?.setOnClickListener {
            sucessfullApplyed()

        }


    }



    private var tempOtp = ""
    private fun dataValid(): Boolean {

        if (binding?.otp1?.text.toString().isEmpty() ||
            binding?.otp2?.text.toString().isEmpty() ||
            binding?.otp3?.text.toString().isEmpty() ||
            binding?.otp4?.text.toString().isEmpty()
        ) {
            showToast("Please enter OTP")
            return false
        }

        tempOtp =
            binding?.otp1?.text.toString() + binding?.otp2?.text.toString() + binding?.otp3?.text.toString() + binding?.otp4?.text.toString()

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }


    private fun sucessfullApplyed() {

        val dialog = Dialog(this@EmailOtpActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogeditemail)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@EmailOtpActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )

        val emailedit = dialog.findViewById<TextInputEditText>(R.id.edemail)
        val righticon = dialog.findViewById<ImageView>(R.id.rightemail)
        val closeIcon = dialog.findViewById<ImageView>(R.id.imageclose)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }


        Glide.with(this@EmailOtpActivity).load(R.mipmap.editemail)
            .into(dialog.findViewById(R.id.imageView13))

        val pro = dialog.findViewById<ProgressBar>(R.id.emailloding)
        val submit = dialog.findViewById<AppCompatButton>(R.id.btnContinue)


        emailedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                Log.d("TAG", "afxddddterTextChanged: " + emailedit.text.toString())

                if (emailedit.text.toString().length > 5) {
                    currentEmailText = emailedit.text.toString()
                    righticon.visibility = View.GONE
                    viewModel.verifyEmail(emailedit.text.toString())

                }


            }

        })



        lifecycleScope.launch {

            viewModel.emailValidationConversion.collect {
                when (it) {
                    CreateProfileViewModel.GetLoginEvent.Empty -> {
                    }

                    is CreateProfileViewModel.GetLoginEvent.Failure -> {
                        pro.visibility = View.GONE
                        emailInValid = false

                    }

                    CreateProfileViewModel.GetLoginEvent.Loading -> {
                        pro.visibility = View.VISIBLE

                    }

                    is CreateProfileViewModel.GetLoginEvent.Success -> {
                        pro.visibility = View.GONE

                        if (currentEmailText == emailedit.text.toString()) {
                            emailInValid = true
                            righticon.visibility = View.VISIBLE
                        } else {
                            emailInValid = false
                            righticon.visibility = View.GONE

                        }

                        if (!Constant.checkEmail(emailedit.text.toString().trim())) {
                            righticon.visibility = View.GONE
                        }


                    }
                }
            }

        }

        lifecycleScope.launch {

            viewModelupdate.registerConversion.collect {
                when (it) {
                    EditprofileSetDataViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Failure -> {

                        Utility.hideProgressBar()
                    }

                    EditprofileSetDataViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is EditprofileSetDataViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()
                        //"Email OTP Sent"
                        if (it.result.message == "We have send you verify mail in your email account, Please check and verify!") {

                        } else {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }


        submit.setOnClickListener {
            if (isdatavalid(emailedit.text.toString())) {
                emailChangeObserve = true
                tempUserDataObject?.email = binding?.txtemail?.text.toString()
                binding?.txtemail?.text = emailedit.text.toString()
                updateData(emailedit.text.toString().trim())


                dialog.dismiss()


            }

        }


        dialog.show()

    }

    private fun isdatavalid(EditEmail: String): Boolean {
        if (EditEmail.trim().isEmpty()) {
            showToast("Please enter email.")
            return false

        } else if (emailInValid == null || emailInValid == false) {
            showToast("This email is already registered.")
            return false


        } else if (emailInValid == null || emailInValid == false) {
            showToast("This email is already registered.")
            return false

        } else if (EditEmail.trim() == binding?.txtemail?.text.toString()) {
            showToast("OTP is already sent on this mail address.")
            return false
        } else if (!Constant.checkEmail(EditEmail.trim())) {
            showToast("Please enter valid email.")
            return false

        }

        return true

    }


    private fun updateData(editemail: String) {
        tempUserDataObject?.let { it1 ->
            viewModelupdate.updateEditUserData(
                first_name = it1.first_name,
                last_name = it1.last_name,
                email = editemail,
                gender = it1.gender,
                interested = it1.intrested,

                ukeylooking_for = it1.ukeylooking_for,
                ukeylanguage = it1.ukeylanguage,
                ukeyrelationstatus = it1.ukeyrelationship_status,

                ukeyeducation = it1.ukeyeducation,

                lat = it1.latitude,
                long = it1.longitude,
                image1 = it1.imageUrl1,
                image2 = it1.imageUrl2,
                image3 = it1.imageUrl3,
                image4 = it1.imageUrl4,
                image5 = it1.imageUrl5,
                image6 = it1.imageUrl6,
                about = it1.about,
                job_title = it1.job_title,
                address = it1.address,
                dob = it1.dob,
                profile_video = it1.profile_video,
                height = it1.height
            )

        }
    }


}