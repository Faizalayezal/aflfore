package com.foreverinlove.network.repository

import android.util.Log
import com.foreverinlove.network.model.ApiServiceClass
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.model.reqBody
import com.foreverinlove.network.response.AddContectSupportResponse
import com.foreverinlove.network.response.AddReviewResponse
import com.foreverinlove.network.response.AddSuggestionResponse
import com.foreverinlove.network.response.AddtionalQueListResponse
import com.foreverinlove.network.response.BaseResponse
import com.foreverinlove.network.response.ConsumeGroupVideoCallResponse
import com.foreverinlove.network.response.CreateProfileResponse
import com.foreverinlove.network.response.CurrentFreeUserPlanResponse
import com.foreverinlove.network.response.CurrentUserPlanResponse
import com.foreverinlove.network.response.DiscoverUserListResponse
import com.foreverinlove.network.response.EmailResendResponse
import com.foreverinlove.network.response.EmailVarificationResponse
import com.foreverinlove.network.response.GetMemberList
import com.foreverinlove.network.response.GetProfileResponse
import com.foreverinlove.network.response.GetRequestedListResponse
import com.foreverinlove.network.response.GetRoomListResponse
import com.foreverinlove.network.response.GetmessageconversationResponse
import com.foreverinlove.network.response.LikesListResponse
import com.foreverinlove.network.response.Notificationresponse
import com.foreverinlove.network.response.OldMessageListResponse
import com.foreverinlove.network.response.PagesResponse
import com.foreverinlove.network.response.PendingPopupListResponse
import com.foreverinlove.network.response.PrivateChatListResponse
import com.foreverinlove.network.response.PrivateChatResponse
import com.foreverinlove.network.response.ReasonListResponse
import com.foreverinlove.network.response.ReviewResponse
import com.foreverinlove.network.response.SettingResponse
import com.foreverinlove.network.response.SignInResponse
import com.foreverinlove.network.response.SubscriptionPlanListResponse
import com.foreverinlove.network.response.SuperLikePlanResponse
import com.foreverinlove.network.response.SuperLikePurchaseResponse
import com.foreverinlove.network.response.SwipeResponse
import com.foreverinlove.network.response.UserDetailsresponse
import com.foreverinlove.network.response.VideoCallResponse
import com.foreverinlove.network.response.ViewedMeListResponse
import com.foreverinlove.network.response.onFidoData
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject


class DefaultMainRepository @Inject constructor(
    private val apiServiceClass: ApiServiceClass
) : MainRepository {

    //Api chalu hoy screen chnage thy tyare aavta toeast mate
    private val requestedWith = "XMLHttpRequest"

    private fun <T> handleException(e: Exception): Resource<T> {
        println("kanyeWestApi $e")
        return if (e.toString() == "java.net.SocketTimeoutException: timeout") {
            Resource.Error("Weak internet connection. Try again later.")
        } else if (e.toString().contains("kotlinx.coroutines.JobCancellationException")) {
            Resource.Error("")
        } else if (e.toString().contains("Expected BEGIN_ARRAY but was BEGIN_OBJECT")) {
            Resource.Error("")
        } else {
            Resource.Error("An $e occurred")
        }
    }

    override suspend fun addtionalQuiestion(
        token: String
    ): Resource<AddtionalQueListResponse> {
        return try {

            val response = apiServiceClass.getAdditonalQuiestion(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getPendingPopupList(
        token: String
    ): Resource<PendingPopupListResponse> {
        return try {

            val response = apiServiceClass.getPendingPopupList(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getSignIn(phoneNumber: String): Resource<SignInResponse> {
        return try {
            val response = apiServiceClass.getSignInApi(phoneNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }


    override suspend fun getOtp(
        phone: String,
        login_otp: String,
        device_type: String,
        fcm_token: String
    ): Resource<CreateProfileResponse> {
        return try {
            val response = apiServiceClass.getOtpApi(phone, login_otp, device_type, fcm_token)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getEmailIn(email: String): Resource<CreateProfileResponse> {
        return try {
            val response = apiServiceClass.getEmailApi(email.reqBody())
            Log.d("TAG", "getEmailIn: " + email)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getRegister(
        first_name: String,
        last_name: String,
        email: String,
        gender: String,
        intrested: String,
        job_title: String,
        dob: String,
        height: String,
        ukeylooking_for: String,
        ukeylanguage: String,
        ukeyrelationship_status: String,
        ukeyeducation: String,
        address: String,
        imageStr1: String,
        imageStr2: String,
        imageStr3: String,
        imageStr4: String,
        imageStr5: String,
        imageStr6: String,
        latitude: String,
        longitude: String,
        about: String,
        profile_video: String,
        api_token: String,
    ): Resource<CreateProfileResponse> {

        val imageStrArray = ArrayList<String>()
        if (imageStr1 != "") imageStrArray.add(imageStr1)
        if (imageStr2 != "") imageStrArray.add(imageStr2)
        if (imageStr3 != "") imageStrArray.add(imageStr3)
        if (imageStr4 != "") imageStrArray.add(imageStr4)
        if (imageStr5 != "") imageStrArray.add(imageStr5)
        if (imageStr6 != "") imageStrArray.add(imageStr6)

        return try {
            var imagePart1: MultipartBody.Part? = null
            var imagePart2: MultipartBody.Part? = null
            var imagePart3: MultipartBody.Part? = null
            var imagePart4: MultipartBody.Part? = null
            var imagePart5: MultipartBody.Part? = null
            var imagePart6: MultipartBody.Part? = null
            var profilevideoo: MultipartBody.Part? = null
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"


            try {
                imageStrArray.forEachIndexed { index, str ->
                    if (str != "" && File(str).exists()) {
                        when (index) {
                            0 -> {
                                imagePart1 = MultipartBody.Part.createFormData(
                                    "images[0]",
                                    "myPic1",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            1 -> {
                                imagePart2 = MultipartBody.Part.createFormData(
                                    "images[1]",
                                    "myPic2",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            2 -> {
                                imagePart3 = MultipartBody.Part.createFormData(
                                    "images[2]",
                                    "myPic3",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            3 -> {
                                imagePart4 = MultipartBody.Part.createFormData(
                                    "images[3]",
                                    "myPic4",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            4 -> {
                                imagePart5 = MultipartBody.Part.createFormData(
                                    "images[4]",
                                    "myPic5",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            5 -> {
                                imagePart6 = MultipartBody.Part.createFormData(
                                    "images[5]",
                                    "myPic6",
                                    File(str).asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }
                        }
                    }
                }
                if (profile_video != "" && File(profile_video).exists()) {
                    profilevideoo = MultipartBody.Part.createFormData(
                        "profile_video",
                        "myVid",
                        File(profile_video).asRequestBody("video/*".toMediaTypeOrNull())
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }

            var response: Response<CreateProfileResponse>? = null
            response = apiServiceClass.getRegisterUserApi(
                fnm = first_name.reqBody(),
                lnm = last_name.reqBody(),
                email = email.reqBody(),
                gender = gender.reqBody(),
                interestedIn = intrested.reqBody(),
                jobtitle = job_title.reqBody(),
                dob = dob.reqBody(),
                address = address.reqBody(),
                height = height.reqBody(),
                looking_forQuery2 = ukeylooking_for.reqBody(),
                userLookingForQuery = ukeylooking_for.reqBody(),
                language = ukeylanguage.reqBody(),
                relationshipstatus = ukeyrelationship_status.reqBody(),
                education = ukeyeducation.reqBody(),
                image1 = imagePart1,
                image2 = imagePart2,
                image3 = imagePart3,
                image4 = imagePart4,
                image5 = imagePart5,
                image6 = imagePart6,
                latitude = latitude.reqBody(),
                longitude = longitude.reqBody(),
                about = about.reqBody(),
                queLookingFor = ukeylooking_for.reqBody(),
                profile_video = profilevideoo,
                auth = headers
            )

            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun ReadMessage(
        token: String,
        matchid: String,
        type: String
    ): Resource<String> {

        return try {

            val response = apiServiceClass.readmessage(
                "Bearer $token", requestedWith, matchid, type
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }

    }

    override suspend fun sendEmailOtp(
        email: String,
        email_verified_otp: String,
        api_token: String,
    ): Resource<CreateProfileResponse> {
        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"

            val response = apiServiceClass.emailSendOtp(
                email.reqBody(),
                email_verified_otp.reqBody(),
                headers
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun ProfilesendEmailOtp(
        email: String,
        email_verified_otp: String,
        api_token: String,
    ): Resource<CreateProfileResponse> {
        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"
            val response = apiServiceClass.profileEmailSendOtp(
                email.reqBody(),
                email_verified_otp.reqBody(),
                headers
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else Resource.Error("An Error occurred")
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun getResendEmailOtpApi(
        email: String, token: String,
    ): Resource<EmailResendResponse> {
        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $token"
            headers["X-Requested-With"] = "XMLHttpRequest"
            val response = apiServiceClass.emailResendOtpApi(email.reqBody(), headers)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else Resource.Error("An Error occurred")
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun getProfileResendEmailOtpApi(
        email: String,
        api_token: String,
    ): Resource<EmailResendResponse> {
        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"

            val response = apiServiceClass.profileEmailResendOtpApi(
                email.reqBody(), headers
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getSwipeProfile(
        token: String,
        status: String,
        userId: String
    ): Resource<SwipeResponse> {
        return try {
            val map = HashMap<String, String>()
            map["status"] = status
            map["user_id"] = userId

            val response = apiServiceClass.swipeImage(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun ReviewLatterProfile(
        token: String,
        userId: String
    ): Resource<AddReviewResponse> {

        return try {

            val response = apiServiceClass.AddToReviewLatter(
                "Bearer $token", requestedWith, userId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }

    }

    override suspend fun RequestedRoom(
        token: String,
        userId: String
    ): Resource<GetRequestedListResponse> {
        return try {

            val response = apiServiceClass.requestedRoom(
                "Bearer $token", requestedWith, userId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun LeaveRoom(
        token: String,
        roomId: String
    ): Resource<GetRequestedListResponse> {
        return try {

            val response = apiServiceClass.leaveRoom(
                "Bearer $token", requestedWith, roomId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getDiscoverUserList(
        token: String,
        data: DiscoverFilterObject
    ): Resource<DiscoverUserListResponse> {
        return try {
            val map = HashMap<String, String>()

            if ((data.min_age ?: "") != "") map["min_age"] = data.min_age ?: ""
            if ((data.max_age ?: "") != "") map["max_age"] = data.max_age ?: ""
            if ((data.latitude ?: "") != "") map["latitude"] = data.latitude ?: ""
            if ((data.longitude ?: "") != "") map["longitude"] = data.longitude ?: ""
            if ((data.page ?: "") != "") map["page"] = data.page ?: ""
            if ((data.pageSize ?: "") != "") map["pageSize"] = data.pageSize ?: ""
            if ((data.education ?: "") != "") map["education"] = data.education ?: ""
            if ((data.minDistance ?: "") != "") map["min_distance"] = data.minDistance ?: ""
            if ((data.maxDistance ?: "") != "") map["max_distance"] = data.maxDistance ?: ""
            if ((data.arts ?: "") != "") map["arts"] = data.arts ?: ""
            if ((data.covidVaccine ?: "") != "") map["covid_vaccine"] = data.covidVaccine ?: ""
            if ((data.dietaryLifestyle ?: "") != "") map["dietary_lifestyle"] =
                data.dietaryLifestyle ?: ""
            if ((data.drink ?: "") != "") map["drink"] = data.drink ?: ""
            if ((data.drugs ?: "") != "") map["drugs"] = data.drugs ?: ""
            if ((data.firstDateIceBreaker ?: "") != "") map["first_date_ice_breaker"] =
                data.firstDateIceBreaker ?: ""
            if ((data.horoscope ?: "") != "") map["horoscope"] = data.horoscope ?: ""
            if ((data.interests ?: "") != "") map["interests"] = data.interests ?: ""
            if ((data.language ?: "") != "") map["language"] = data.language ?: ""
            //if((""?:"") != "")map["user_looking_for"] = ""?:""
            if ((data.listedLookingFor ?: "") != "") map["looking_for"] =
                data.listedLookingFor ?: ""
            if ((data.pets ?: "") != "") map["pets"] = data.pets ?: ""
            if ((data.politicalLeaning ?: "") != "") map["political_leaning"] =
                data.politicalLeaning ?: ""
            if ((data.relationshipStatus ?: "") != "") map["relationship_status"] =
                data.relationshipStatus ?: ""
            if ((data.religion ?: "") != "") map["religion"] = data.religion ?: ""
            if ((data.smoking ?: "") != "") map["smoking"] = data.smoking ?: ""
            if ((data.isApplyFilter ?: "") != "") map["is_apply_filter"] =
                data.isApplyFilter ?: ""
            if ((data.minHeight ?: "") != "") map["min_height"] = data.minHeight ?: ""
            if ((data.maxHeight ?: "") != "") map["max_height"] = data.maxHeight ?: ""
            if ((data.hobbies ?: "") != "") map["hobbies"] = data.hobbies ?: ""
            if ((data.address ?: "") != "") map["address"] = data.address ?: ""


            val response = apiServiceClass.getDiscoverUserList(
                params = map,
                token = "Bearer $token",
                with = requestedWith
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun updateSettingsData(
        token: String,
        type: SettingsViewModel.UpdateSettingType
    ): Resource<SettingResponse> {
        return try {
            val map = HashMap<String, String>()

            when (type) {

                is SettingsViewModel.UpdateSettingType.UpdateDistanceVisible -> {
                    if (type.distanceVisible == "0")
                        map["distance_unit"] = "Km"
                    else if (type.distanceVisible == "1")
                        map["distance_unit"] = "Mile"
                }

                is SettingsViewModel.UpdateSettingType.UpdateShowNotification -> {
                    map["show_notification"] = type.showNotification
                }

                is SettingsViewModel.UpdateSettingType.UpdateSendEmail ->
                    map["send_mail"] = type.email
            }

            Log.d("TAG", "updateSettingsDatasdfdf: " + map)
            val response = apiServiceClass.getSettingsData(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getSetting(api_token: String): Resource<SettingResponse> {
        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"

            val response = apiServiceClass.getSettingApi(headers)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getReviewLatterList(token: String): Resource<ReviewResponse> {
        return try {

            val response = apiServiceClass.getReviewLatter(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getGetLikeMeList(token: String): Resource<LikesListResponse> {
        return try {

            val response = apiServiceClass.getWhoLikeMe(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun getMyLikeList(token: String): Resource<LikesListResponse> {
        return try {

            val response = apiServiceClass.getMyLikes(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getMessageConverterList(token: String): Resource<GetmessageconversationResponse> {
        return try {

            val response = apiServiceClass.getMessageConversation(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getUserMessagesList(token: String): Resource<OldMessageListResponse> {
        return try {
            val response = apiServiceClass.getMessagesList(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }

        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun logoutUser(token: String): Resource<String> {
        return try {

            val response = apiServiceClass.getLogout(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun sendChatMessage(
        match_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse> {
        return try {
            val map = HashMap<String, String>()
            map["match_id"] = match_id
            map["message"] = message
            val response = apiServiceClass.sendChatMessage(
                requestedWith, "Bearer $token", map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun sendGroupChatMessage(
        room_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse> {
        return try {
            val map = HashMap<String, String>()
            map["room_id"] = room_id
            map["message"] = message
            val response = apiServiceClass.sendGroupChatMessage(
                requestedWith, "Bearer $token", map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun sendPrivateChatMessage(
        match_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse> {
        return try {
            val map = HashMap<String, String>()
            map["match_id"] = match_id
            map["message"] = message
            val response = apiServiceClass.sendPrivateChatMessage(
                requestedWith, "Bearer $token", map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getReasonList(token: String): Resource<ReasonListResponse> {
        return try {

            val map = HashMap<String, String>()
            map["ab"] = "ab"
            map["report_type"] = "report"

            val response = apiServiceClass.getReasonData(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getProfileFieldDetail(token: String): Resource<GetProfileResponse> {

        return try {
            val response = apiServiceClass.getProfileFieldDetail(
                "Bearer $token", requestedWith
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getSubscriptionPlanList(token: String): Resource<SubscriptionPlanListResponse> {
        return try {
            val response = apiServiceClass.getSubscriptionPlanList(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun openUserDetails(
        token: String,
        userId: String
    ): Resource<UserDetailsresponse> {
        return try {

            val response = apiServiceClass.openUserDetails(
                "Bearer $token", requestedWith, userId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getAddViewCount(token: String, userId: String): Resource<SettingResponse> {
        return try {
            val map = HashMap<String, String>()

            map["viewer_id"] = userId

            val response = apiServiceClass.getAddView(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getGetViewProfileList(token: String): Resource<ViewedMeListResponse> {
        return try {

            val response = apiServiceClass.getWhoViewMe(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }



    override suspend fun getRemoveImage(
        imagekey: String,
        api_token: String
    ): Resource<CreateProfileResponse> {

        return try {
            val headers: MutableMap<String, String> = HashMap()
            headers["Authorization"] = "Bearer $api_token"
            headers["X-Requested-With"] = "XMLHttpRequest"

            val response = apiServiceClass.getRemoveImage(
                imagekey.reqBody(),
                headers
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun reportUnmatchedPerson(
        token: String,
        user_id: String,
        report_id: String,
        type: String
    ): Resource<ViewedMeListResponse> {
        return try {

            val map = HashMap<String, String>()
            map["user_id"] = user_id
            map["report_id"] = report_id
            map["type"] = type

            val response = apiServiceClass.getReportUnmatche(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun videoCall(
        token: String,
        user_id: String,
        status: String
    ): Resource<VideoCallResponse> {
        return try {

            val map = HashMap<String, String>()
            map["user_id"] = user_id
            map["status"] = status

            val response = apiServiceClass.videoCall(
                "Bearer $token", requestedWith, map
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getNotification(
        token: String
    ): Resource<Notificationresponse> {
        return try {

                val response = apiServiceClass.getNotification(
                    requestedWith, "Bearer $token"
                )
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }


    override suspend fun updatePhaseData(
        education: String,
        lookingFor: String,
        dietaryLifestyle: String,
        pets: String,
        arts: String,
        language: String,
        interests: String,
        drink: String,
        drugs: String,
        horoscope: String,
        religion: String,
        politicalLeaning: String,
        relationshipStatus: String,
        lifeStyle: String,
        firstDateIceBreaker: String,
        covidVaccine: String,
        smoking: String,
        token: String,
        firstName: String,
        lastName: String,
        dob: String,
        email: String,
        gender: String,
        jobTitle: String,
        about: String,
        lookingForQuery: String,
        usersLookingForQuery: String,
    ): Resource<CreateProfileResponse> {

        return try {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer $token"
                headers["X-Requested-With"] = "XMLHttpRequest"

                var response: Response<CreateProfileResponse>? = null
                response = apiServiceClass.updatePhaseData(
                    education = education.reqBody(),
                    lookingFor = lookingFor.reqBody(),
                    dietaryLifestyle = dietaryLifestyle.reqBody(),
                    pets = pets.reqBody(),
                    arts = arts.reqBody(),
                    language = language.reqBody(),
                    interests = interests.reqBody(),
                    drink = drink.reqBody(),
                    drugs = drugs.reqBody(),
                    horoscope = horoscope.reqBody(),
                    religion = religion.reqBody(),
                    politicalLeaning = politicalLeaning.reqBody(),
                    relationshipStatus = relationshipStatus.reqBody(),
                    lifeStyle = lifeStyle.reqBody(),
                    firstDateIceBreaker = firstDateIceBreaker.reqBody(),
                    covidVaccine = covidVaccine.reqBody(),
                    smoking = smoking.reqBody(),
                    firstName = firstName.reqBody(),
                    lastName = lastName.reqBody(),
                    dob = dob.reqBody(),
                    email = email.reqBody(),
                    gender = gender.reqBody(),
                    jobTitle = jobTitle.reqBody(),
                    about = about.reqBody(),
                    lookingForQuery = lookingForQuery.reqBody(),
                    usersLookingForQuery = usersLookingForQuery.reqBody(),
                    auth = headers
                )


                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }


    override suspend fun purchasePlan(
        planId: String,
        coins: String,
        token: String,
    ): Resource<CreateProfileResponse> {
        return try {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer $token"
                headers["X-Requested-With"] = "XMLHttpRequest"

                var response: Response<CreateProfileResponse>? = null
                response = apiServiceClass.purchasePlan(
                    planId = planId.reqBody(),
                    coins = coins.reqBody(),
                    auth = headers
                )


                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }



    override suspend fun getRoomList(token: String): Resource<GetRoomListResponse> {
        return try {

                val response = apiServiceClass.getRoomList(
                    requestedWith, "Bearer $token",
                )
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }


    override suspend fun getJoinList(token: String): Resource<GetRoomListResponse> {
        return try {

                val response = apiServiceClass.getJoinList(
                    requestedWith, "Bearer $token",
                )
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }


    override suspend fun getRequestList(token: String): Resource<GetRoomListResponse> {
        return try {

                val response = apiServiceClass.getRequestList(
                    requestedWith, "Bearer $token",
                )
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }



    override suspend fun sendMessagePrivate(
        token: String,
        userId: String,
        usermsg: String
    ): Resource<PrivateChatResponse> {
        return try {

                val response =
                    apiServiceClass.sendMessagePrivate(
                        "Bearer $token",
                        requestedWith,
                        userId,
                        usermsg
                    )
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    Resource.Success(result)
                } else {
                    Resource.Error("An Error occurred")
                }
            } catch (e: Exception) {
                handleException(e)

            }
        }


    override suspend fun confirmPrivateChat(
        token: String,
        userId: String
    ): Resource<PrivateChatListResponse> {
        return try {

            val response =
                apiServiceClass.confirmPrivateChat("Bearer $token", requestedWith, userId)
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun rejectPrivateChat(
        token: String,
        userId: String
    ): Resource<PrivateChatListResponse> {
        return try {

            val response =
                apiServiceClass.rejectPrivateChat("Bearer $token", requestedWith, userId)
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getCurrentUserPlan(token: String): Resource<CurrentUserPlanResponse> {
        return try {

            val response =
                apiServiceClass.getCurrentUserPlan(requestedWith, "Bearer $token")
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getPrivateChatListList(
        token: String,
        status: String
    ): Resource<PrivateChatListResponse> {
        return try {

            val response = apiServiceClass.getsendMessagePrivatList(
                requestedWith, "Bearer $token", status
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getSuperLikeList(
        token: String,
        type: String
    ): Resource<SuperLikePlanResponse> {
        return try {

            val response = apiServiceClass.getSuperPlan(
                requestedWith, "Bearer $token", type
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun startGroupVideoCall(
        token: String,
        roomId: String
    ): Resource<BaseResponse> {
        return try {

            val response = apiServiceClass.startVideoCallApi(
                "Bearer $token", requestedWith, roomId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun endGroupVideoCall(token: String, roomId: String): Resource<BaseResponse> {
        return try {

            val response = apiServiceClass.endGroupVideoCallApi(
                "Bearer $token", requestedWith, roomId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun consumeGroupVideoCall(
        token: String,
        roomId: String
    ): Resource<ConsumeGroupVideoCallResponse> {
        return try {

            val response = apiServiceClass.consumeGroupVideoCall(
                "Bearer $token", requestedWith, roomId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun getMemberListVideoCall(
        token: String,
        roomId: String
    ): Resource<GetMemberList> {
        return try {

            val response = apiServiceClass.getMemberListVideoCallApi(
                "Bearer $token", requestedWith, roomId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun updateUidVideoCall(
        token: String,
        roomId: String,
        uId: String
    ): Resource<BaseResponse> {
        return try {

            val response = apiServiceClass.updateUidVideoCallApi(
                "Bearer $token", requestedWith, roomId, uId
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun superLike(
        token: String,
        productId: String
    ): Resource<SuperLikePurchaseResponse> {
        return try {

            val response =
                apiServiceClass.superLike("Bearer $token", requestedWith, productId)
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun freePlan(token: String): Resource<CurrentFreeUserPlanResponse> {
        return try {

            val response =
                apiServiceClass.freePlan("Bearer $token", requestedWith, "abc")
            val result = response.body()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun addContactSupport(
        token: String,
        name: String,
        email: String,
        description: String
    ): Resource<AddContectSupportResponse> {
        return try {


            val response = apiServiceClass.addContactSupport(
                "Bearer $token", requestedWith, name, email, description
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun addSuggestion(
        token: String,
        description: String
    ): Resource<AddSuggestionResponse> {
        return try {


            val response = apiServiceClass.addSuggestion(
                "Bearer $token", requestedWith, description
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun addPages(): Resource<PagesResponse> {
        return try {

            val response = apiServiceClass.getPages(requestedWith)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun readPopupData(token: String, screenId: String): Resource<PagesResponse> {
        return try {

            val response = apiServiceClass.viewPopup("Bearer $token", requestedWith, screenId)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


    override suspend fun readNotification(token: String): Resource<AddReviewResponse> {
        return try {
            val response = apiServiceClass.readNotification(
                requestedWith, "Bearer $token"
            )
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }

    override suspend fun onFido(firstName: String, lastName: String): Resource<onFidoData> {
        return try {

            val response = apiServiceClass.onFido(requestedWith,firstName, lastName)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("An Error occurred")
            }
        } catch (e: Exception) {
            handleException(e)

        }
    }


}