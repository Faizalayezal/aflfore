package com.foreverinlove.viewmodels

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.network.response.SignInResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        generateFirebaseToken()
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch {
                    it.printStackTrace()
                }
                .collect {

                    tempUserDataObject = it

                }
        }
    }


    //*************************************************************
    //Resend Otp Api Flow

    sealed class GetLoginEvent {
        class Success(val result: SignInResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    private val _loginConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val loginConversion: StateFlow<GetLoginEvent> = _loginConversion

    fun getResendOtp() {

        viewModelScope.launch {
            _loginConversion.value = GetLoginEvent.Loading
            when (val data = repository.getSignIn(tempUserDataObject?.phone ?: "")) {
                is Resource.Error -> {
                    _loginConversion.value = GetLoginEvent.Failure(data.message!!)
                }

                is Resource.Success -> {

                    if (data.data!!.status == 1) {
                        _loginConversion.value = GetLoginEvent.Success(
                            data.data
                        )
                    } else {
                        _loginConversion.value = GetLoginEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }
            }
        }
    }

    //*************************************************************

    //*************************************************************
    //Otp Api Flow

    sealed class GetOtpEvent {
        class Success(val result: CreateProfileResponse) : GetOtpEvent()
        class Failure(val errorText: String) : GetOtpEvent()
        object Loading : GetOtpEvent()
        object Empty : GetOtpEvent()
    }

    private val _OtpConversion = MutableStateFlow<GetOtpEvent>(GetOtpEvent.Empty)
    val OtpConversion: StateFlow<GetOtpEvent> = _OtpConversion
    private var fcmToken = ""
    private fun generateFirebaseToken() {
        val fireInstance = FirebaseMessaging.getInstance()

        viewModelScope.launch {
            delay(1000)
            fireInstance.token.addOnSuccessListener { token: String ->
                if (!TextUtils.isEmpty(token)) {
                    fcmToken = token
                } else {
                }
            }.addOnFailureListener { }.addOnCanceledListener {}
                .addOnCompleteListener { task: Task<String> ->

                }
        }
    }

    fun getOtpStatus(loginOtp: String) {


        viewModelScope.launch {
            _OtpConversion.value = GetOtpEvent.Loading

            when (val data =
                repository.getOtp(tempUserDataObject?.phone ?: "", loginOtp, "android", fcmToken)) {
                is Resource.Error -> {
                    _OtpConversion.value = GetOtpEvent.Failure(data.message!!)
                }

                is Resource.Success -> {

                    if (data.data!!.status == 1) {
                        viewModelScope.launch {
                            tempUserDataObject?.id = (data.data.data?.user?.id).toString()
                            tempUserDataObject?.first_name = data.data.data?.user?.first_name ?: ""
                            tempUserDataObject?.last_name = data.data.data?.user?.last_name ?: ""
                            tempUserDataObject?.dob = data.data.data?.user?.dob ?: ""
                            tempUserDataObject?.age = data.data.data?.user?.age ?: ""
                            tempUserDataObject?.email = data.data.data?.user?.email ?: ""
                            tempUserDataObject?.gender = data.data.data?.user?.gender ?: ""
                            tempUserDataObject?.intrested =
                                data.data.data?.user?.user_intrested_in ?: ""
                            tempUserDataObject?.job_title = data.data.data?.user?.job_title ?: ""
                            tempUserDataObject?.google_id = data.data.data?.user?.google_id ?: ""
                            tempUserDataObject?.fb_id = data.data.data?.user?.fb_id ?: ""
                            tempUserDataObject?.apple_id = data.data.data?.user?.apple_id ?: ""
                            tempUserDataObject?.login_type = data.data.data?.user?.login_type ?: ""
                            tempUserDataObject?.otp_expird_time =
                                data.data.data?.user?.otp_expird_time ?: ""
                            tempUserDataObject?.address = data.data.data?.user?.address ?: ""
                            tempUserDataObject?.latitude = data.data.data?.user?.latitude ?: ""
                            tempUserDataObject?.longitude = data.data.data?.user?.longitude ?: ""
                            tempUserDataObject?.height = data.data.data?.user?.height ?: ""
                            tempUserDataObject?.emailVerified =
                                (data.data.data?.user?.email_verified).toString()

                            tempUserDataObject?.imageUrl1 =
                                data.data.data?.user?.user_images?.getOrNull(0)?.url ?: ""
                            tempUserDataObject?.imageUrl2 =
                                data.data.data?.user?.user_images?.getOrNull(1)?.url ?: ""
                            tempUserDataObject?.imageUrl3 =
                                data.data.data?.user?.user_images?.getOrNull(2)?.url ?: ""
                            tempUserDataObject?.imageUrl4 =
                                data.data.data?.user?.user_images?.getOrNull(3)?.url ?: ""
                            tempUserDataObject?.imageUrl5 =
                                data.data.data?.user?.user_images?.getOrNull(4)?.url ?: ""
                            tempUserDataObject?.imageUrl6 =
                                data.data.data?.user?.user_images?.getOrNull(5)?.url ?: ""

                            tempUserDataObject?.imageId1 =
                                (data.data.data?.user?.user_images?.getOrNull(0)?.id
                                    ?: "").toString()
                            tempUserDataObject?.imageId2 =
                                (data.data.data?.user?.user_images?.getOrNull(1)?.id
                                    ?: "").toString()
                            tempUserDataObject?.imageId3 =
                                (data.data.data?.user?.user_images?.getOrNull(2)?.id
                                    ?: "").toString()
                            tempUserDataObject?.imageId4 =
                                (data.data.data?.user?.user_images?.getOrNull(3)?.id
                                    ?: "").toString()
                            tempUserDataObject?.imageId5 =
                                (data.data.data?.user?.user_images?.getOrNull(4)?.id
                                    ?: "").toString()
                            tempUserDataObject?.imageId6 =
                                (data.data.data?.user?.user_images?.getOrNull(5)?.id
                                    ?: "").toString()

                            tempUserDataObject?.profile_video =
                                data.data.data?.user?.profile_video ?: ""
                            tempUserDataObject?.lastseen = data.data.data?.user?.lastseen ?: ""
                            tempUserDataObject?.fcm_token = data.data.data?.user?.fcm_token ?: ""
                            tempUserDataObject?.token = data.data.data?.token ?: ""

                            tempUserDataObject?.let { context.dataStoreSetUserData(it) }

                        }

                        _OtpConversion.value = GetOtpEvent.Success(
                            data.data
                        )
                    } else {
                        _OtpConversion.value = GetOtpEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }
            }

        }

    }



}