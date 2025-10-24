package com.foreverinlove.viewmodels

import android.app.Application
import android.content.ContextWrapper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foreverinlove.Constant.deletePhoto
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.network.response.CreateProfileResponseRelationship
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
class AddProfilePictureViewModel @Inject constructor(
    application: Application, val repository: MainRepository
) : BaseViewModel(application) {

    sealed class GetLoginEvent {
        class Success(val result: CreateProfileResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    private val _loginConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val loginConversion: StateFlow<GetLoginEvent> = _loginConversion

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject = it

                }
        }
    }


    fun setUserData(
        img1: String,
        img2: String,
        img3: String,
        img4: String,
        img5: String,
        img6: String
    ) {

        viewModelScope.launch {
            _loginConversion.value = GetLoginEvent.Loading
            when (val data = repository.getRegister(
                first_name = tempUserDataObject?.first_name ?: "",
                last_name = tempUserDataObject?.last_name ?: "",
                email = tempUserDataObject?.email ?: "",
                gender = tempUserDataObject?.gender ?: "",
                interested = tempUserDataObject?.intrested ?: "",
                job_title = tempUserDataObject?.job_title ?: "",
                dob = tempUserDataObject?.dob ?: "",
                height = tempUserDataObject?.height ?: "",
                ukeylooking_for = tempUserDataObject?.ukeylooking_for ?: "",
                ukeylanguage = tempUserDataObject?.ukeylanguage ?: "",
                ukeyrelationship_status = tempUserDataObject?.ukeyrelationship_status ?: "",
                ukeyeducation = tempUserDataObject?.ukeyeducation ?: "",

                address = tempUserDataObject?.address ?: "",
                imageStr1 = img1,
                imageStr2 = img2,
                imageStr3 = img3,
                imageStr4 = img4,
                imageStr5 = img5,
                imageStr6 = img6,
                about = tempUserDataObject?.about ?: "",
                profile_video = tempUserDataObject?.profile_video ?: "",
                api_token = tempUserDataObject?.token ?: "",

                latitude = tempUserDataObject?.latitude?:"",
                longitude = tempUserDataObject?.longitude?:""

            )) {
                is Resource.Error -> {
                    _loginConversion.value = GetLoginEvent.Failure(data.message!!)
                }
                is Resource.Success -> {
                    if (data.data!!.status == 1) {

                        val ab = TempUserDataObject(
                            login_otp = data.data.data?.user?.login_otp ?: "",
                            token_type = data.data.data?.token_type ?: "",
                            session_id = "",
                            userStatus = data.data.data?.user?.status ?: "",
                            phone = data.data.data?.user?.phone ?: "",


                            id = (data.data.data?.user?.id ?: "").toString(),
                            first_name = data.data.data?.user?.first_name ?: "",
                            last_name = data.data.data?.user?.last_name ?: "",
                            dob = data.data.data?.user?.dob ?: "",
                            age = data.data.data?.user?.age ?: "",
                            email = data.data.data?.user?.email ?: "",
                            emailVerified = (data.data.data?.user?.email_verified ?:"").toString(),
                            gender = data.data.data?.user?.gender ?: "",
                            intrested = data.data.data?.user?.user_intrested_in ?: "",
                            job_title = data.data.data?.user?.job_title ?: "",
                            google_id = data.data.data?.user?.google_id ?: "",
                            fb_id = data.data.data?.user?.fb_id ?: "",
                            apple_id = data.data.data?.user?.apple_id ?: "",
                            login_type = data.data.data?.user?.login_type ?: "",
                            otp_expird_time = data.data.data?.user?.otp_expird_time ?: "",
                            address = data.data.data?.user?.address ?: "",
                            latitude = data.data.data?.user?.latitude ?: "",
                            longitude = data.data.data?.user?.longitude ?: "",
                            height = data.data.data?.user?.height ?: "",

                            ukeyeducation= data.data.data?.user?.user_educations?.question_id ?:"",
                            ukeylooking_for=data.data.data?.user?.user_looking_for?.firstOrNull()?.question_id?:"",
                            ukeyrelationship_status =data.data.data?.user?.user_relationship_status?.question_id?:"",
                            ukeylanguage =getLanguageIdString(data.data.data?.user?.user_language),


                            about = data.data.data?.user?.about ?: "",
                            imageUrl1 = data.data.data?.user?.user_images?.getOrNull(0)?.url ?: "",
                            imageUrl2 = data.data.data?.user?.user_images?.getOrNull(1)?.url ?: "",
                            imageUrl3 = data.data.data?.user?.user_images?.getOrNull(2)?.url ?: "",
                            imageUrl4 = data.data.data?.user?.user_images?.getOrNull(3)?.url ?: "",
                            imageUrl5 = data.data.data?.user?.user_images?.getOrNull(4)?.url ?: "",
                            imageUrl6 = data.data.data?.user?.user_images?.getOrNull(5)?.url ?: "",
                            imageId1 = (data.data.data?.user?.user_images?.getOrNull(0)?.id ?: "").toString(),
                            imageId2 = (data.data.data?.user?.user_images?.getOrNull(1)?.id ?: "").toString(),
                            imageId3 = (data.data.data?.user?.user_images?.getOrNull(2)?.id ?: "").toString(),
                            imageId4 = (data.data.data?.user?.user_images?.getOrNull(3)?.id ?: "").toString(),
                            imageId5 = (data.data.data?.user?.user_images?.getOrNull(4)?.id ?: "").toString(),
                            imageId6 = (data.data.data?.user?.user_images?.getOrNull(5)?.id ?: "").toString(),
                            profile_video = data.data.data?.user?.profile_video ?: "",
                            lastseen = data.data.data?.user?.lastseen ?: "",
                            fcm_token = data.data.data?.user?.fcm_token ?: "",
                            token = data.data.data?.user?.api_token ?: "",
                            registerFlowStatus = null
                        )

                        context.dataStoreSetUserData(ab)
                        Log.d("TAG", "profiledsfsdfdfgdfg: "+ab)
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
    private fun listToString(list:List<String>?):String{
        var str = ""
        list?.let{
            for(i in it.indices){
                str = if(str==""){
                    it[i]
                } else{
                    str+","+it[i]
                }
            }
        }
        return str
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            ContextWrapper(context).deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }



}