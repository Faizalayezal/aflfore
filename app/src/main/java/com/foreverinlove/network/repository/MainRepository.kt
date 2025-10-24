package com.foreverinlove.network.repository

import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.response.*
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.viewmodels.SettingsViewModel

interface MainRepository {

    suspend fun addtionalQuiestion(
        token: String,
    ): Resource<AddtionalQueListResponse>

    suspend fun getPendingPopupList(
        token: String,
    ): Resource<PendingPopupListResponse>

    suspend fun getSignIn(phoneNumber: String): Resource<SignInResponse>

    suspend fun getOtp(
        phone: String,
        login_otp: String,
        device_type: String,
        fcm_token: String
    ): Resource<CreateProfileResponse>

    suspend fun ReadMessage(token: String, matchid: String,type:String): Resource<String>

    suspend fun getEmailIn(email: String): Resource<CreateProfileResponse>

    suspend fun getRegister(
        first_name: String,
        last_name: String,
        email: String,
        gender: String,
        interested: String,
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
        api_token: String
    ): Resource<CreateProfileResponse>


    suspend fun sendEmailOtp(
        email: String,
        email_verified_otp: String,
        api_token: String
    ): Resource<CreateProfileResponse>
    suspend fun ProfilesendEmailOtp (
        email: String,
        email_verified_otp: String,
        api_token: String
    ): Resource<CreateProfileResponse>



    suspend fun getResendEmailOtpApi(email: String,token: String): Resource<EmailResendResponse>
    suspend fun getProfileResendEmailOtpApi(email: String,token: String): Resource<EmailResendResponse>



    suspend fun getDiscoverUserList(
        token: String, data: DiscoverFilterObject
    ): Resource<DiscoverUserListResponse>


    suspend fun getSwipeProfile(
        token: String,
        status: String,
        userId: String
    ): Resource<SwipeResponse>

    suspend fun ReviewLatterProfile(token: String, userId: String): Resource<AddReviewResponse>

    suspend fun RequestedRoom(token: String, userId: String): Resource<GetRequestedListResponse>

    suspend fun LeaveRoom(token: String, roomid: String): Resource<GetRequestedListResponse>

    suspend fun updateSettingsData(token: String, type: SettingsViewModel.UpdateSettingType): Resource<SettingResponse>

    suspend fun getSetting(api_token: String): Resource<SettingResponse>

    suspend fun getReviewLatterList(token: String): Resource<ReviewResponse>

    suspend fun getGetLikeMeList(token: String): Resource<LikesListResponse>
    suspend fun getMyLikeList(token: String): Resource<LikesListResponse>

    suspend fun getGetViewProfileList(token: String): Resource<ViewedMeListResponse>


    suspend fun getAddViewCount(token: String, userId: String): Resource<SettingResponse>

    suspend fun getUserMessagesList(token: String): Resource<OldMessageListResponse>

    suspend fun getMessageConverterList(token: String): Resource<GetmessageconversationResponse>

    suspend fun logoutUser(token: String): Resource<String>

    suspend fun sendChatMessage(
        match_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse>

    suspend fun sendGroupChatMessage(
        room_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse>


    suspend fun sendPrivateChatMessage(
        match_id: String,
        message: String,
        token: String
    ): Resource<EmailVarificationResponse>

    suspend fun getReasonList(token: String): Resource<ReasonListResponse>

    suspend fun getProfileFieldDetail(token: String): Resource<GetProfileResponse>

    suspend fun getSubscriptionPlanList(token: String): Resource<SubscriptionPlanListResponse>

    suspend fun openUserDetails(token: String, userId: String): Resource<UserDetailsresponse>

    suspend fun getRemoveImage(
        imageKey: String,
        api_token: String
    ): Resource<CreateProfileResponse>

    suspend fun reportUnmatchedPerson(
        token: String,
        user_id: String,
        report_id: String,
        type: String,
    ): Resource<ViewedMeListResponse>

    suspend fun videoCall(
        token: String,
        user_id: String,
        status: String,
    ): Resource<VideoCallResponse>


    suspend fun getNotification(
        token: String,
    ): Resource<Notificationresponse>

    suspend fun updatePhaseData(
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
    ): Resource<CreateProfileResponse>

    suspend fun purchasePlan(
        planId: String,
        coins: String,
        token: String
    ): Resource<CreateProfileResponse>

    suspend fun getRoomList(token: String): Resource<GetRoomListResponse>

    suspend fun getJoinList(token: String): Resource<GetRoomListResponse>

    suspend fun getRequestList(token: String): Resource<GetRoomListResponse>

    suspend fun getPrivateChatListList(token: String,status:String): Resource<PrivateChatListResponse>

    suspend fun getSuperLikeList(token: String,type:String): Resource<SuperLikePlanResponse>

    suspend fun startGroupVideoCall(token: String,roomId:String): Resource<BaseResponse>

    suspend fun endGroupVideoCall(token: String,roomId:String): Resource<BaseResponse>

    suspend fun consumeGroupVideoCall(token: String,roomId:String): Resource<ConsumeGroupVideoCallResponse>
    suspend fun getMemberListVideoCall(token: String,roomId:String): Resource<GetMemberList>
    suspend fun updateUidVideoCall(token: String,roomId:String,uId:String): Resource<BaseResponse>

    suspend fun superLike(token: String,productId: String):Resource<SuperLikePurchaseResponse>

    suspend fun freePlan(token: String):Resource<CurrentFreeUserPlanResponse>


    suspend fun sendMessagePrivate(token: String, userId: String, usermsg: String): Resource<PrivateChatResponse>

    suspend fun confirmPrivateChat(token: String,userId: String):Resource<PrivateChatListResponse>

    suspend fun rejectPrivateChat(token: String,userId: String):Resource<PrivateChatListResponse>

    suspend fun getCurrentUserPlan(token: String):Resource<CurrentUserPlanResponse>

    suspend fun addContactSupport(token: String,name: String,email: String,description: String): Resource<AddContectSupportResponse>

    suspend fun addSuggestion(token: String,description: String): Resource<AddSuggestionResponse>

    suspend fun addPages():Resource<PagesResponse>
    suspend fun readPopupData(token: String,screenId: String):Resource<PagesResponse>

    suspend fun readNotification(
        token: String,
    ): Resource<AddReviewResponse>

    suspend fun onFido(firstName: String,lastName: String):Resource<onFidoData>





}
