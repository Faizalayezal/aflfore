package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.TempCreateProfileObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    application: Application, val repository: MainRepository
) : BaseViewModel(application) {

    sealed class GetLoginEvent {
        class Success(val result: CreateProfileResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch { it.printStackTrace() }.collect {

                    tempUserDataObject = it
                }
        }
    }

    fun updateUserData(data: TempCreateProfileObject) {

        viewModelScope.launch {
            val tempData = TempUserDataObject(
                height = data.height,
                first_name = data.fnm,
                last_name = data.lnm,

                ukeyeducation = data.ukeyeducation,
                ukeylanguage = data.ukeylanguage,
                ukeyrelationship_status = data.ukeyrelationstatus,
                ukeylooking_for = data.ukeylookingFor,
                latitude = data.lat,
                longitude = data.long,
                email = data.email,
                gender = data.gender,
                intrested = data.looking_for,
                dob = data.dob,
                job_title = data.jobTitle,

                address = data.address,
                profile_video = data.video,
                token = tempUserDataObject?.token ?: "",
                registerFlowStatus = data.registerFlowStatus
            )


            context.dataStoreSetUserData(tempData)
        }

    }

    private val _emailValidationConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val emailValidationConversion: StateFlow<GetLoginEvent> = _emailValidationConversion

    private var checkEmailJob: Job? = null
    fun verifyEmail(verifyEmail: String) {

        if(verifyEmail.length<4)return


        checkEmailJob?.cancel()

        checkEmailJob = viewModelScope.launch {
            _emailValidationConversion.value = GetLoginEvent.Loading
            when (val data = repository.getEmailIn(email = verifyEmail)) {
                is Resource.Error -> {
                    _emailValidationConversion.value = GetLoginEvent.Failure(data.message!!)
                }
                is Resource.Success -> {
                    if (data.data!!.status == 1) {
                        _emailValidationConversion.value = GetLoginEvent.Success(data.data)
                    } else {
                        _emailValidationConversion.value = GetLoginEvent.Failure(
                            data.data.message ?: ""
                        )
                    }
                }
            }
        }
    }

}



