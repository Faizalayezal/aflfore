package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditprofileSetDataViewModel @Inject constructor(
    application: Application, val repository: MainRepository
) : BaseViewModel(application) {

    sealed class GetLoginEvent {
        class Success(val result: CreateProfileResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    private val _registerConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val registerConversion: StateFlow<GetLoginEvent> = _registerConversion

    private var tempUserEditDataObject: TempUserDataObject? = null


    var tempUserDataObject = MutableLiveData<TempUserDataObject>()
    private var isFirstTime = true

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject.postValue(it)
                    tempUserEditDataObject = it

                    if (isFirstTime)

                        isFirstTime = false
                }
        }
    }

    fun updateEditUserData(
        first_name: String,
        last_name: String,
        email: String,
        gender: String,
        interested: String,

        ukeylooking_for: String,
        ukeylanguage: String,
        ukeyrelationstatus: String,
        ukeyeducation: String,

        dob: String,
        image1: String,
        image2: String,
        image3: String,
        image4: String,
        image5: String,
        image6: String,
        job_title: String,
        height: String,
        address: String,
        about: String,
        lat: String,
        long: String,
        profile_video: String,
    ) {

        viewModelScope.launch {
            _registerConversion.value = GetLoginEvent.Loading
            when (val data = repository.getRegister(
                first_name = first_name,
                last_name = last_name,
                email = email,
                gender = gender,
                interested = interested,
                job_title = job_title,
                dob = dob,
                height = height,

                ukeylooking_for = ukeylooking_for,
                ukeylanguage = ukeylanguage,
                ukeyrelationship_status = ukeyrelationstatus,
                ukeyeducation = ukeyeducation,

                address = address,
                imageStr1 = image1,
                imageStr2 = image2,
                imageStr3 = image3,
                imageStr4 = image4,
                imageStr5 = image5,
                imageStr6 = image6,
                about = about,
                profile_video = profile_video,
                api_token = tempUserEditDataObject?.token ?: "",
                latitude = lat,
                longitude = long
            )) {
                is Resource.Error -> {
                    _registerConversion.value = GetLoginEvent.Failure(data.message!!)
                }

                is Resource.Success -> {
                    if (data.data!!.status == 1) {
                        viewModelScope.launch {
                            val ab = tempUserEditDataObject ?: TempUserDataObject()

                            ab.login_otp = data.data.data?.user?.login_otp ?: ""
                            ab.token_type = data.data.data?.token_type ?: ""
                            ab.session_id = ""
                            ab.userStatus = data.data.data?.user?.status ?: ""
                            ab.phone = data.data.data?.user?.phone ?: ""

                            ab.id = (data.data.data?.user?.id ?: "").toString()
                            ab.first_name = data.data.data?.user?.first_name ?: ""
                            ab.last_name = data.data.data?.user?.last_name ?: ""
                            ab.dob = data.data.data?.user?.dob ?: ""
                            ab.age = data.data.data?.user?.age ?: ""
                            ab.email = data.data.data?.user?.email ?: ""
                            ab.emailVerified =
                                (data.data.data?.user?.email_verified ?: "").toString()

                            ab.gender = data.data.data?.user?.gender ?: ""
                            ab.intrested = data.data.data?.user?.user_intrested_in ?: ""

                            ab.ukeyrelationship_status =
                                data.data.data?.user?.user_relationship_status?.question_id ?: ""
                            ab.ukeyeducation =
                                data.data.data?.user?.user_educations?.question_id ?: ""

                            ab.job_title = data.data.data?.user?.job_title ?: ""
                            ab.google_id = data.data.data?.user?.google_id ?: ""
                            ab.fb_id = data.data.data?.user?.fb_id ?: ""
                            ab.apple_id = data.data.data?.user?.apple_id ?: ""
                            ab.login_type = data.data.data?.user?.login_type ?: ""
                            ab.otp_expird_time = data.data.data?.user?.otp_expird_time ?: ""
                            ab.address = data.data.data?.user?.address ?: ""
                            ab.latitude = data.data.data?.user?.latitude ?: ""
                            ab.longitude = data.data.data?.user?.longitude ?: ""
                            ab.height = data.data.data?.user?.height ?: ""
                            ab.about = data.data.data?.user?.about ?: ""

                            ab.imageUrl1 =
                                data.data.data?.user?.user_images?.getOrNull(0)?.url ?: ""
                            ab.imageUrl2 =
                                data.data.data?.user?.user_images?.getOrNull(1)?.url ?: ""
                            ab.imageUrl3 =
                                data.data.data?.user?.user_images?.getOrNull(2)?.url ?: ""
                            ab.imageUrl4 =
                                data.data.data?.user?.user_images?.getOrNull(3)?.url ?: ""
                            ab.imageUrl5 =
                                data.data.data?.user?.user_images?.getOrNull(4)?.url ?: ""
                            ab.imageUrl6 =
                                data.data.data?.user?.user_images?.getOrNull(5)?.url ?: ""

                            ab.imageId1 = (data.data.data?.user?.user_images?.getOrNull(0)?.id
                                ?: "").toString()
                            ab.imageId2 = (data.data.data?.user?.user_images?.getOrNull(1)?.id
                                ?: "").toString()
                            ab.imageId3 = (data.data.data?.user?.user_images?.getOrNull(2)?.id
                                ?: "").toString()
                            ab.imageId4 = (data.data.data?.user?.user_images?.getOrNull(3)?.id
                                ?: "").toString()
                            ab.imageId5 = (data.data.data?.user?.user_images?.getOrNull(4)?.id
                                ?: "").toString()
                            ab.imageId6 = (data.data.data?.user?.user_images?.getOrNull(5)?.id
                                ?: "").toString()

                            ab.profile_video = data.data.data?.user?.profile_video ?: ""
                            ab.lastseen = data.data.data?.user?.lastseen ?: ""
                            ab.fcm_token = data.data.data?.user?.fcm_token ?: ""
                            ab.token = data.data.data?.user?.api_token ?: ""


                            launch { context.dataStoreSetUserData(ab) }.join()

                            _registerConversion.value = GetLoginEvent.Success(
                                data.data
                            )

                        }

                    } else {
                        _registerConversion.value = GetLoginEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }

            }

        }
    }

    private val _removeConversion = MutableStateFlow<GetLoginEvent>(
        GetLoginEvent.Empty
    )
    val removeConversion: StateFlow<GetLoginEvent> = _removeConversion

    fun removeImage(
        imageKey: String,
    ) {

        viewModelScope.launch {
            _removeConversion.value = GetLoginEvent.Loading
            when (val data = repository.getRemoveImage(
                imageKey,
                api_token = tempUserEditDataObject?.token ?: "",

                )) {
                is Resource.Error -> {
                    _removeConversion.value = GetLoginEvent.Failure(data.message!!)
                }

                is Resource.Success -> {
                    if (data.data!!.status == 1) {
                        _removeConversion.value = GetLoginEvent.Success(
                            data.data
                        )
                    } else {
                        _removeConversion.value = GetLoginEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }

            }

        }

    }
}