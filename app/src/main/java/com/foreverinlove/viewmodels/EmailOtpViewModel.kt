package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.network.response.EmailResendResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailOtpViewModel @Inject constructor(
    private val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null

    var tempEmailUserDataObject = MutableLiveData<TempUserDataObject>()

    //on the sport output mate
    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject = it
                    tempEmailUserDataObject.postValue(it)

                }
        }
    }


    //*************************************************************
    //Resend Otp Api Flow

    private val _resendEMAILOtpConversion =
        MutableStateFlow<ResendEmailOtpEvent>(ResendEmailOtpEvent.Empty)
    val resendEMAILPhoneOtpConversion: StateFlow<ResendEmailOtpEvent> = _resendEMAILOtpConversion

    sealed class ResendEmailOtpEvent {
        class Success(val result: EmailResendResponse) : ResendEmailOtpEvent()
        class Failure(val errorText: String) : ResendEmailOtpEvent()
        object Loading : ResendEmailOtpEvent()
        object Empty : ResendEmailOtpEvent()
    }

    fun getResendOtp(emailStr: String) {


        viewModelScope.launch(Dispatchers.IO) {
            _resendEMAILOtpConversion.value = ResendEmailOtpEvent.Loading
            when (val quotesResponse =
                repository.getResendEmailOtpApi(emailStr, tempUserDataObject?.token ?: "")) {
                is Resource.Error -> _resendEMAILOtpConversion.value =
                    ResendEmailOtpEvent.Failure(quotesResponse.message!!)

                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    _resendEMAILOtpConversion.value = ResendEmailOtpEvent.Success(
                        quote
                    )
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

    private val _otpConversion = MutableStateFlow<GetOtpEvent>(GetOtpEvent.Empty)
    val otpConversion: StateFlow<GetOtpEvent> = _otpConversion

    fun getOtpStatus(emailOtp: String, tempEmail: String? = null, listner: (Int) -> Unit) {


        viewModelScope.launch {
            _otpConversion.value = GetOtpEvent.Loading
            when (val data = repository.sendEmailOtp(
                tempEmail ?: tempUserDataObject?.email ?: "",
                emailOtp,
                tempUserDataObject?.token ?: ""
            )) {
                is Resource.Error -> {
                    _otpConversion.value = GetOtpEvent.Failure(data.message!!)
                }

                is Resource.Success -> {

                    if (data.data?.status == 1) {

                        viewModelScope.launch {
                            tempUserDataObject?.id = (data.data.data?.user?.id).toString()
                            tempUserDataObject?.first_name = data.data.data?.user?.first_name ?: ""
                            tempUserDataObject?.last_name = data.data.data?.user?.last_name ?: ""
                            tempUserDataObject?.dob = data.data.data?.user?.dob ?: ""
                            tempUserDataObject?.age = data.data.data?.user?.age ?: ""
                            tempUserDataObject?.email = tempEmail ?: ""
                            tempUserDataObject?.emailVerified = (data.data.data?.user?.email_verified).toString()
                            tempUserDataObject?.gender = data.data.data?.user?.gender ?: ""
                            tempUserDataObject?.job_title = data.data.data?.user?.job_title ?: ""
                            tempUserDataObject?.google_id = data.data.data?.user?.google_id ?: ""
                            tempUserDataObject?.fb_id = data.data.data?.user?.fb_id ?: ""
                            tempUserDataObject?.apple_id = data.data.data?.user?.apple_id ?: ""
                            tempUserDataObject?.login_type = data.data.data?.user?.login_type ?: ""
                            tempUserDataObject?.otp_expird_time =
                                data.data.data?.user?.otp_expird_time ?: ""
                            tempUserDataObject?.address = data.data.data?.user?.address ?: ""
                            tempUserDataObject?.intrested =
                                data.data.data?.user?.user_intrested_in ?: ""
                            tempUserDataObject?.latitude = data.data.data?.user?.latitude ?: ""
                            tempUserDataObject?.longitude = data.data.data?.user?.longitude ?: ""
                            tempUserDataObject?.height = data.data.data?.user?.height ?: ""

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


                        _otpConversion.value = GetOtpEvent.Success(
                            data.data
                        )
                        listner.invoke(data.data.status?:0)
                    } else {
                        _otpConversion.value = GetOtpEvent.Failure(
                            data.data?.message ?: ""
                        )
                    }
                }
            }

        }

    }


    //Edit profile mathi jay tyare Otp Api Flow
    //*************************************************************


    //Otp Api Flow

    sealed class GetProfileOtpEvent {
        class Success(val result: CreateProfileResponse) : GetProfileOtpEvent()
        class Failure(val errorText: String) : GetProfileOtpEvent()
        object Loading : GetProfileOtpEvent()
        object Empty : GetProfileOtpEvent()
    }

    private val _profileOtpConversion =
        MutableStateFlow<GetProfileOtpEvent>(GetProfileOtpEvent.Empty)
    val profileOtpConversion: StateFlow<GetProfileOtpEvent> = _profileOtpConversion

    fun getProfileOtpStatus(emailOtp: String, tempEmail: String? = null) {

        viewModelScope.launch {
            _profileOtpConversion.value = GetProfileOtpEvent.Loading
            when (val data = repository.ProfilesendEmailOtp(
                tempEmail ?: tempUserDataObject?.email ?: "",
                emailOtp,
                tempUserDataObject?.token ?: ""
            )) {
                is Resource.Error -> {
                    _profileOtpConversion.value = GetProfileOtpEvent.Failure(data.message!!)
                }

                is Resource.Success -> {

                    if (data.data!!.status == 1) {

                        GlobalScope.launch {
                            tempUserDataObject?.id = (data.data.data?.user?.id).toString()
                            tempUserDataObject?.first_name = data.data.data?.user?.first_name ?: ""
                            tempUserDataObject?.last_name = data.data.data?.user?.last_name ?: ""
                            tempUserDataObject?.dob = data.data.data?.user?.dob ?: ""
                            tempUserDataObject?.age = data.data.data?.user?.age ?: ""
                            tempUserDataObject?.email = data.data.data?.user?.email ?: ""
                            tempUserDataObject?.emailVerified =
                                (data.data.data?.user?.email_verified).toString()
                            tempUserDataObject?.gender = data.data.data?.user?.gender ?: ""
                            tempUserDataObject?.job_title = data.data.data?.user?.job_title ?: ""
                            tempUserDataObject?.google_id = data.data.data?.user?.google_id ?: ""
                            tempUserDataObject?.fb_id = data.data.data?.user?.fb_id ?: ""
                            tempUserDataObject?.apple_id = data.data.data?.user?.apple_id ?: ""
                            tempUserDataObject?.login_type = data.data.data?.user?.login_type ?: ""
                            tempUserDataObject?.otp_expird_time =
                                data.data.data?.user?.otp_expird_time ?: ""
                            tempUserDataObject?.address = data.data.data?.user?.address ?: ""
                            tempUserDataObject?.intrested =
                                data.data.data?.user?.user_intrested_in ?: ""
                            tempUserDataObject?.latitude = data.data.data?.user?.latitude ?: ""
                            tempUserDataObject?.longitude = data.data.data?.user?.longitude ?: ""
                            tempUserDataObject?.height = data.data.data?.user?.height ?: ""

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


                        _profileOtpConversion.value = GetProfileOtpEvent.Success(
                            data.data
                        )
                    } else {
                        _profileOtpConversion.value = GetProfileOtpEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }
            }

        }

    }


    //*************************************************************
    //Resend Otp Api Flow

    sealed class ResendProfileEmailOtpEvent {
        class Success(val result: EmailResendResponse) : ResendProfileEmailOtpEvent()
        class Failure(val errorText: String) : ResendProfileEmailOtpEvent()
        object Loading : ResendProfileEmailOtpEvent()
        object Empty : ResendProfileEmailOtpEvent()
    }

    private val _profileResendEMAILOtpConversion =
        MutableStateFlow<ResendProfileEmailOtpEvent>(ResendProfileEmailOtpEvent.Empty)
    val profileResendEMAILPhoneOtpConversion: StateFlow<ResendProfileEmailOtpEvent> =
        _profileResendEMAILOtpConversion

    fun getProfileResendOtp(emailStr: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _profileResendEMAILOtpConversion.value = ResendProfileEmailOtpEvent.Loading
            when (val quotesResponse =
                repository.getProfileResendEmailOtpApi(emailStr, tempUserDataObject?.token ?: "")) {
                is Resource.Error -> _profileResendEMAILOtpConversion.value =
                    ResendProfileEmailOtpEvent.Failure(quotesResponse.message!!)

                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    _profileResendEMAILOtpConversion.value = ResendProfileEmailOtpEvent.Success(
                        quote
                    )
                }
            }
        }
    }


}
