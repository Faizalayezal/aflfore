package com.foreverinlove.network.model

import com.foreverinlove.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceClass {
    //file jati hoyne tyare multipart key word aave


    //1
    @GET("get_question_list")
    suspend fun getAdditonalQuiestion(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
        //  @Path("question") question_type: String,
    ): Response<AddtionalQueListResponse>

    //2
    @GET("pending_popup_list")
    suspend fun getPendingPopupList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
    ): Response<PendingPopupListResponse>


    //3
    @FormUrlEncoded
    @POST("login")
    suspend fun getSignInApi(
        @Field("phone") phone: String?,
    ): Response<SignInResponse>

    //4
    @FormUrlEncoded
    @POST("user_conformation")
    suspend fun getOtpApi(
        @Field("phone") phone: String?,
        @Field("login_otp") login_otp: String?,
        @Field("device_type") device_type: String?,
        @Field("fcm_token") fcm_token: String?,
    ): Response<CreateProfileResponse>

    //3
    @JvmSuppressWildcards
    @Multipart
    @POST("update_profile")
    suspend fun getRegisterUserApi(
        @Part("first_name") fnm: RequestBody,
        @Part("last_name") lnm: RequestBody,
        @Part("email") email: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("job_title") jobtitle: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("address") address: RequestBody,
        @Part("height") height: RequestBody,

        @Part("user_intrested_in") interestedIn: RequestBody,
        @Part("userQuestion[looking_for]") queLookingFor: RequestBody,
        @Part("users_looking_for") userLookingForQuery: RequestBody,
        @Part("looking_for") looking_forQuery2: RequestBody,

        @Part("userQuestion[language]") language: RequestBody,
        @Part("userQuestion[relationship_status]") relationshipstatus: RequestBody,
        @Part("userQuestion[education]") education: RequestBody,


        @Part image1: MultipartBody.Part?,
        @Part image2: MultipartBody.Part?,
        @Part image3: MultipartBody.Part?,
        @Part image4: MultipartBody.Part?,
        @Part image5: MultipartBody.Part?,
        @Part image6: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("about") about: RequestBody,
        @Part profile_video: MultipartBody.Part?,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>

    //4
    @JvmSuppressWildcards
    @Multipart
    @POST("update_profile")
    suspend fun updatePhaseData(

        @Part("userQuestion[education]") education: RequestBody,
        @Part("userQuestion[looking_for]") lookingFor: RequestBody,
        @Part("userQuestion[dietary_lifestyle]") dietaryLifestyle: RequestBody,
        @Part("userQuestion[pets]") pets: RequestBody,
        @Part("userQuestion[arts]") arts: RequestBody,
        @Part("userQuestion[language]") language: RequestBody,
        @Part("userQuestion[interests]") interests: RequestBody,
        @Part("userQuestion[drink]") drink: RequestBody,
        @Part("userQuestion[drugs]") drugs: RequestBody,
        @Part("userQuestion[horoscope]") horoscope: RequestBody,
        @Part("userQuestion[religion]") religion: RequestBody,
        @Part("userQuestion[political_leaning]") politicalLeaning: RequestBody,
        @Part("userQuestion[relationship_status]") relationshipStatus: RequestBody,
        @Part("userQuestion[life_style]") lifeStyle: RequestBody,
        @Part("userQuestion[first_date_ice_breaker]") firstDateIceBreaker: RequestBody,
        @Part("userQuestion[covid_vaccine]") covidVaccine: RequestBody,
        @Part("userQuestion[smoking]") smoking: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("email") email: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("job_title") jobTitle: RequestBody,
        @Part("about") about: RequestBody,
        @Part("looking_for") lookingForQuery: RequestBody,//intrested in male famale others
        @Part("users_looking_for") usersLookingForQuery: RequestBody, //casual reletionship varu

        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>

    //5
    @JvmSuppressWildcards
    @Multipart
    @POST("purchase_plan")
    suspend fun purchasePlan(
        @Part("plan_id") planId: RequestBody,
        @Part("coins") coins: RequestBody,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>


    @JvmSuppressWildcards
    @Multipart
    @POST("update_profile")
    suspend fun getPhaseRegisterUserApi(
        @Part("userQuestion[looking_for]") lookingfor: RequestBody,
        @Part("userQuestion[language]") language: RequestBody,
        @Part("userQuestion[relationship_status]") relationshipstatus: RequestBody,
        @Part("userQuestion[education]") education: RequestBody,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>

    //6
    @JvmSuppressWildcards
    @Multipart
    @POST("check_email")
    suspend fun getEmailApi(
        @Part("email") email: RequestBody,
    ): Response<CreateProfileResponse>

    //7
    @GET("get_profile")
    suspend fun getProfileFieldDetail(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
    ): Response<GetProfileResponse>

    //8
    @JvmSuppressWildcards
    @Multipart
    @POST("email_verification")
    suspend fun emailSendOtp(
        @Part("email") email: RequestBody,
        @Part("otp") email_verified_otp: RequestBody,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>

    @JvmSuppressWildcards
    @Multipart
    @POST("profile_email_verification")
    suspend fun profileEmailSendOtp(
        @Part("email") email: RequestBody,
        @Part("otp") email_verified_otp: RequestBody,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>


    //9
    @JvmSuppressWildcards
    @Multipart
    @POST("resend_otp")
    suspend fun emailResendOtpApi(
        @Part("email") email: RequestBody,
        @HeaderMap auth: Map<String, String>,
        //@Field("fcm_token") fcm_token: String?,
    ): Response<EmailResendResponse>


    @JvmSuppressWildcards
    @Multipart
    @POST("profile_resend_otp")
    suspend fun profileEmailResendOtpApi(
        @Part("email") email: RequestBody,
        @HeaderMap auth: Map<String, String>,
        //@Field("fcm_token") fcm_token: String?,
    ): Response<EmailResendResponse>

    //10
    @FormUrlEncoded
    @POST("discover")
    suspend fun getDiscoverUserList(
        @FieldMap params: Map<String, String>,
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
    ): Response<DiscoverUserListResponse>

    /*
        @Field("min_age") min_age: String?,
        @Field("max_age") max_age: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("page") page: String?,
        @Field("pageSize") pageSize: String?,
        @Field("education") education: String?,
        @Field("min_distance") minDistance: String?,
        @Field("max_distance") maxDistance: String?,
        @Field("arts") arts: String?,
        @Field("covid_vaccine") covidVaccine: String?,
        @Field("dietary_lifestyle") dietaryLifestyle: String?,
        @Field("drink") drink: String?,
        @Field("drugs") drugs: String?,
        @Field("first_date_ice_breaker") firstDateIceBreaker: String?,
        @Field("horoscope") horoscope: String?,
        @Field("interests") interests: String?,
        @Field("language") language: String?,
        @Field("user_looking_for") userLookingFor: String?,
        @Field("looking_for") listedLookingFor: String?,
        @Field("pets") pets: String?,
        @Field("political_leaning") politicalLeaning: String?,
        @Field("relationship_status") relationshipStatus: String?,
        @Field("religion") religion: String?,
        @Field("smoking") smoking: String?,
        @Field("is_apply_filter") isApplyFilter: String?,
        @Field("min_height") minHeight: String?,
        @Field("max_height") maxHeight: String?,
        @Field("hobbies") hobbies: String?,*/

    //11
    @FormUrlEncoded
    @POST("read_conversation")
    suspend fun readmessage(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("match_id") matchid: String?,
        @Field("type") type: String?,
    ): Response<String>

    //12
    @FormUrlEncoded
    @POST("get_user_details")
    suspend fun openUserDetails(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("user_id") userid: String?,
    ): Response<UserDetailsresponse>

    //13
    @FormUrlEncoded
    @POST("add_to_review_latter")
    suspend fun AddToReviewLatter(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("user_id") userid: String?,
    ): Response<AddReviewResponse>

    //14
    @GET("get_review_latter_list")
    suspend fun getReviewLatter(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<ReviewResponse>

    //15
    @FormUrlEncoded
    @POST("swipe_profile")
    suspend fun swipeImage(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<SwipeResponse>

    //16
    @FormUrlEncoded
    @POST("user_settings")
    suspend fun getSettingsData(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<SettingResponse>

    //17
    @GET("get_user_settings")
    suspend fun getSettingApi(
        @HeaderMap auth: Map<String, String>,
    ): Response<SettingResponse>

    //18
    @GET("get_who_like_me")
    suspend fun getWhoLikeMe(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<LikesListResponse>

    //18
    @GET("get_like_by_me")
    suspend fun getMyLikes(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<LikesListResponse>

    //19
    @JvmSuppressWildcards
    @Multipart
    @POST("remove_profile_image")
    suspend fun getRemoveImage(
        @Part("image_id") imagekey: RequestBody,
        @HeaderMap auth: Map<String, String>,
    ): Response<CreateProfileResponse>

    //20
    @GET("logout")
    suspend fun getLogout(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<String>

    /* @POST("get_message_conversation")
     suspend fun getmessageconversation(
         @Header("X-Requested-With") with: String,
         @Header("Authorization") token: String
     ): Response<GetmessageconversationResponse>*/

    //21
    @FormUrlEncoded
    @POST("get_reson")
    suspend fun getReasonData(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<ReasonListResponse>

    //22
    @GET("get_plan_list")
    suspend fun getSubscriptionPlanList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<SubscriptionPlanListResponse>

    //23
    @GET("get_who_view_me")
    suspend fun getWhoViewMe(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<ViewedMeListResponse>


    //24
    @FormUrlEncoded
    @POST("who_view_me")
    suspend fun getAddView(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<SettingResponse>

    //25
    @GET("match_details")
    suspend fun getMessageConversation(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<GetmessageconversationResponse>

    //26
    @GET("message_conversation")
    suspend fun getMessagesList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<OldMessageListResponse>


    //27
    @FormUrlEncoded
    @POST("send_message")
    suspend fun sendChatMessage(
        @Header("X-Requested-With") with: String?,
        @Header("Authorization") token: String?,
        @FieldMap params: Map<String, String>
    ): Response<EmailVarificationResponse>


    @FormUrlEncoded
    @POST("send_group_message")
    suspend fun sendGroupChatMessage(
        @Header("X-Requested-With") with: String?,
        @Header("Authorization") token: String?,
        @FieldMap params: Map<String, String>
    ): Response<EmailVarificationResponse>


    //27
    @FormUrlEncoded
    @POST("send_private_message")
    suspend fun sendPrivateChatMessage(
        @Header("X-Requested-With") with: String?,
        @Header("Authorization") token: String?,
        @FieldMap params: Map<String, String>
    ): Response<EmailVarificationResponse>


    //28
    @FormUrlEncoded
    @POST("report_user")
    suspend fun getReportUnmatche(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<ViewedMeListResponse>



    @GET("get_notification")
    suspend fun getNotification(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,

        ): Response<Notificationresponse>

    //30
    @GET("get_rooms_list")
    suspend fun getRoomList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<GetRoomListResponse>

    //31
    @FormUrlEncoded
    @POST("requested_join_room")
    suspend fun requestedRoom(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") userid: String?,
    ): Response<GetRequestedListResponse>

    //32
    @GET("get_join_rooms")
    suspend fun getJoinList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<GetRoomListResponse>

    //33
    @GET("get_requested_rooms")
    suspend fun getRequestList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String
    ): Response<GetRoomListResponse>

    //34
    @FormUrlEncoded
    @POST("leave_room")
    suspend fun leaveRoom(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") userid: String?,
    ): Response<GetRequestedListResponse>

    //35
    @FormUrlEncoded
    @POST("request_to_private_chat")
    suspend fun sendMessagePrivate(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("user_id") userid: String?,
        @Field("invite_msg") usermsg: String?,
    ): Response<PrivateChatResponse>

    //36
    @FormUrlEncoded
    @POST("confirm_private_chat_request")
    suspend fun confirmPrivateChat(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("user_id") userid: String?,
    ): Response<PrivateChatListResponse>

    //37
    @FormUrlEncoded
    @POST("reject_private_chat_request")
    suspend fun rejectPrivateChat(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("user_id") userid: String?,
    ): Response<PrivateChatListResponse>


    //38
    @GET("get_private_all_request_list")
    suspend fun getsendMessagePrivatList(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
        @Query("status") status: String?,

        ): Response<PrivateChatListResponse>

    //38
    @GET("get_user_plan")
    suspend fun getCurrentUserPlan(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
    ): Response<CurrentUserPlanResponse>

    @FormUrlEncoded
    @POST("active_free_trial")
    suspend fun freePlan(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("abc") userid: String?,
        ): Response<CurrentFreeUserPlanResponse>

    //38
    @GET("get_product_list")
    suspend fun getSuperPlan(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
        @Query("product_type") type: String,
    ): Response<SuperLikePlanResponse>

    @FormUrlEncoded
    @POST("purchase_super_like")
    suspend fun superLike(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("product_id") userid: String?,
    ): Response<SuperLikePurchaseResponse>



    //video call na data clear krva mate
    @FormUrlEncoded
    @POST("end_video_call_for_group")
    suspend fun endGroupVideoCallApi(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") roomId: String?,
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("group_call_request")
    suspend fun startVideoCallApi(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") roomId: String?,
    ): Response<BaseResponse>

    //status 1 aave tyare call thy
    @FormUrlEncoded
    @POST("consume_on_going_call")
    suspend fun consumeGroupVideoCall(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") roomId: String?,
    ): Response<ConsumeGroupVideoCallResponse>




    //member add thy tyare member get krva mate
    @FormUrlEncoded
    @POST("get_members_list")
    suspend fun getMemberListVideoCallApi(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") roomId: String?,
    ): Response<GetMemberList>



    //aapdu uid update krva mate
    @FormUrlEncoded
    @POST("update_uid")
    suspend fun updateUidVideoCallApi(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("room_id") roomId: String?,
        @Field("u_id") uId: String?,
    ): Response<BaseResponse>


    @FormUrlEncoded
    @POST("single_video_call")
    suspend fun videoCall(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @FieldMap params: Map<String, String>
    ): Response<VideoCallResponse>

    @FormUrlEncoded
    @POST("add_contact_support")
    suspend fun addContactSupport(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("description") description: String?,

        ): Response<AddContectSupportResponse>

    @FormUrlEncoded
    @POST("add_suggestion")
    suspend fun addSuggestion(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("suggestion_desc") desc: String?,
    ): Response<AddSuggestionResponse>




    @FormUrlEncoded
    @POST("view_popup")
    suspend fun viewPopup(
        @Header("Authorization") token: String,
        @Header("X-Requested-With") with: String?,
        @Field("screen_id") screen_id: String?,
    ): Response<PagesResponse>

    @GET("pages")
    suspend fun getPages(
        @Header("X-Requested-With") with: String,
    ): Response<PagesResponse>

    @GET("read_all_notification")
    suspend fun readNotification(
        @Header("X-Requested-With") with: String,
        @Header("Authorization") token: String,
    ): Response<AddReviewResponse>


    @FormUrlEncoded
    @POST("get-onfido-sdk-token")
    suspend fun onFido(
        @Header("X-Requested-With") with: String,
        @Field("first_name") first_name: String?,
        @Field("last_name") last_name: String?,
    ): Response<onFidoData>




}

//APi un usefull

//forget_password
//forget_password_verification
//update_new_password
//change_password

//get_message_conversation
//update_user_lastseen
//get_profile_field_details
//user_register
//signin
//update_token
//get_height_list