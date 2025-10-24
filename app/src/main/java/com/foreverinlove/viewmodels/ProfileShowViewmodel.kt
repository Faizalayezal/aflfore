package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.*
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileShowViewmodel @Inject constructor(
    application: Application, val repository: MainRepository
) : BaseViewModel(application) {
    sealed class GetLoginEvent {
        class Success(val result: SettingResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    var tempUserDataObject: TempUserDataObject? = null

    interface OnDataGet {
        fun onGet(tempData: TempUserDataObject)
    }

    private var isFirstTime = true

    fun start(onDataGet: OnDataGet) {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject = it
                    onDataGet.onGet(it)

                    if (isFirstTime)
                        getData()

                    isFirstTime = false
                }

        }


    }

    fun logoutApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logoutUser(tempUserDataObject?.token ?: "")
        }
    }

    sealed class ProfileFieldResponseEvent {
        class Success(val result: GetProfileResponse) : ProfileFieldResponseEvent()
        class Failure(val errorText: String) : ProfileFieldResponseEvent()
        object Loading : ProfileFieldResponseEvent()
        object Empty : ProfileFieldResponseEvent()
    }

    //mutuble value edit mate
    private val _viewedMeListConversion = MutableStateFlow<ProfileFieldResponseEvent>(
        ProfileFieldResponseEvent.Empty
    )
    val viewedMeListConversion: StateFlow<ProfileFieldResponseEvent> = _viewedMeListConversion

    fun getData() {
        viewModelScope.launch {
            _viewedMeListConversion.value = ProfileFieldResponseEvent.Loading
            when (val quotesResponse =
                repository.getProfileFieldDetail(tempUserDataObject?.token ?: "")) {
                is Resource.Error -> _viewedMeListConversion.value =
                    ProfileFieldResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _viewedMeListConversion.value = ProfileFieldResponseEvent.Success(
                        quote
                    )
                    updateuserdata(quotesResponse.data.data)

                }
            }

        }
    }

    private fun updateuserdata(data: CreateProfileResponseUser?) {

        viewModelScope.launch {
            val tempData = tempUserDataObject
            tempData?.first_name = data?.first_name ?: ""
            tempData?.last_name = data?.last_name ?: ""
            tempData?.intrested = data?.user_intrested_in ?: ""
            tempData?.dob = data?.dob ?: ""
            tempData?.age = data?.age ?: ""
            tempData?.email = data?.email ?: ""
            tempData?.gender = data?.gender ?: ""
            tempData?.job_title = data?.job_title ?: ""
            tempData?.address = data?.address ?: ""
            tempData?.latitude = data?.latitude ?: ""
            tempData?.longitude = data?.longitude ?: ""
            tempData?.height = data?.height ?: ""
            tempData?.about = data?.about ?: ""
            tempData?.is_once_purchased = data?.is_once_purchased ?: false

            tempData?.imageUrl1 = data?.user_images?.getOrNull(0)?.url ?: ""
            tempData?.imageUrl2 = data?.user_images?.getOrNull(1)?.url ?: ""
            tempData?.imageUrl3 = data?.user_images?.getOrNull(2)?.url ?: ""
            tempData?.imageUrl4 = data?.user_images?.getOrNull(3)?.url ?: ""
            tempData?.imageUrl5 = data?.user_images?.getOrNull(4)?.url ?: ""
            tempData?.imageUrl6 = data?.user_images?.getOrNull(5)?.url ?: ""

            tempData?.imageId1 = (data?.user_images?.getOrNull(0)?.id ?: "").toString()
            tempData?.imageId2 = (data?.user_images?.getOrNull(1)?.id ?: "").toString()
            tempData?.imageId3 = (data?.user_images?.getOrNull(2)?.id ?: "").toString()
            tempData?.imageId4 = (data?.user_images?.getOrNull(3)?.id ?: "").toString()
            tempData?.imageId5 = (data?.user_images?.getOrNull(4)?.id ?: "").toString()
            tempData?.imageId6 = (data?.user_images?.getOrNull(5)?.id ?: "").toString()


            tempData?.profile_video = data?.profile_video ?: ""
            tempData?.ukeyrelationship_status =
                data?.user_relationship_status?.question_id ?: ""

            tempData?.ukeylooking_for =getLanguageIdString(data?.user_looking_for)
            tempData?.ukeylanguage = getLanguageIdString(data?.user_language)

            tempData?.ukeysmoking = data?.user_smoking?.question_id?:""

            tempData?.ukeyeducation = data?.user_educations?.question_id?:""

            tempData?.ukeydrugs = data?.user_drugs?.question_id?:""
            tempData?.ukeydrink = data?.user_drink?.question_id?:""
            tempData?.ukeydietary_lifestyle = getLanguageIdString(data?.user_dietary_lifestyle)
            tempData?.ukeyinterests = getLanguageIdString(data?.user_interests)
            tempData?.ukeypets = getLanguageIdString(data?.user_pets)
            tempData?.ukeyhoroscope = data?.user_horoscope?.question_id?:""
            tempData?.ukeypolitical_leaning = data?.user_political_leaning?.question_id?:""
            tempData?.ukeyreligion = data?.user_religion?.question_id?:""
            tempData?.ukeycovid_vaccine = data?.user_covid_vaccine?.question_id?:""
            tempData?.ukeyfirst_date_ice_breaker = data?.user_first_date_ice_breaker?.question_id?:""
            tempData?.ukeyarts = getLanguageIdString(data?.user_arts)


            if (tempData != null) {
                context.dataStoreSetUserData(tempData)
            }
            viewModelScope.launch {
                delay(5000)

                context.dataStoreGetUserData().firstOrNull {


                    true
                }
            }
        }
    }

    private fun getLanguageIdString(userLanguage: List<CreateProfileResponseRelationship>?): String {
        var str = ""

        userLanguage?.forEach {
            str = if (str == "") {
                it.question_id ?: ""
            } else {
                str + "," + (it.question_id ?: "")
            }
        }

        return str
    }

}