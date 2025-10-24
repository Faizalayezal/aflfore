package com.foreverinlove.screen.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.AcceptedChatAdepter
import com.foreverinlove.chatflow.PersonalChatActivity
import com.foreverinlove.databinding.FragmentAcceptedBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.response.PrivateUserSendChatDataList
import com.foreverinlove.network.response.RequestSentUserList
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.GetRequestedChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AcceptedFragment : Fragment(R.layout.fragment_accepted) {

    private lateinit var binding: FragmentAcceptedBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAcceptedBinding.bind(view)


        Glide.with(requireContext()).load(R.mipmap.acceptscreen).into(binding.emptyimg)
        lifecycleScope.launch {
            viewModelTop?.acceptedListConversion?.collect { apiData ->
                when (apiData) {
                    GetRequestedChatViewModel.AcceptedListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is GetRequestedChatViewModel.AcceptedListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(apiData.errorText)

                    }

                    is GetRequestedChatViewModel.AcceptedListResponseEvent.Loading -> {

                    }

                    is GetRequestedChatViewModel.AcceptedListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (apiData.result.status == 1) {

                            val newList = ArrayList<PrivateUserSendChatDataList>()
                            // data set krva mate 2 differt object sander recevice ne srkhi list show thy
                            newList.addAll(apiData.result.data?.request_sent_users ?: listOf())
                            val abList = apiData.result.data?.request_reacived_users?.map {
                                PrivateUserSendChatDataList(

                                    user_private_chat_id = it.user_private_chat_id,
                                    request_from = it.request_from,
                                    request_to = it.request_to,
                                    request_status = it.request_status,
                                    invite_msg = it.invite_msg,
                                    created_at = it.created_at,
                                    updated_at = it.updated_at,
                                    match_id = it.match_id,
                                    last_message = it.last_message,
                                    message_count = it.message_count,
                                    unread_message_count = it.unread_message_count,
                                    sender_id = it.sender_id,
                                    get_request_to_user = RequestSentUserList(
                                        id = it.get_request_from_user?.id,
                                        first_name = it.get_request_from_user?.first_name,
                                        last_name = it.get_request_from_user?.last_name,
                                        dob = it.get_request_from_user?.dob,
                                        age = it.get_request_from_user?.age,
                                        email = it.get_request_from_user?.email,
                                        status = it.get_request_from_user?.status,
                                        phone = it.get_request_from_user?.phone,
                                        gender = it.get_request_from_user?.gender,
                                        job_title = it.get_request_from_user?.job_title,
                                        login_otp = it.get_request_from_user?.login_otp,
                                        google_id = it.get_request_from_user?.google_id,
                                        fb_id = it.get_request_from_user?.fb_id,
                                        apple_id = it.get_request_from_user?.apple_id,
                                        login_type = it.get_request_from_user?.login_type,
                                        otp_expird_time = it.get_request_from_user?.otp_expird_time,
                                        address = it.get_request_from_user?.address,
                                        latitude = it.get_request_from_user?.latitude,
                                        longitude = it.get_request_from_user?.longitude,
                                        profile_video = it.get_request_from_user?.profile_video,
                                        lastseen = it.get_request_from_user?.lastseen,
                                        fcm_token = it.get_request_from_user?.fcm_token,
                                        api_token = it.get_request_from_user?.api_token,
                                        device_type = it.get_request_from_user?.device_type,
                                        about = it.get_request_from_user?.about,
                                        email_verified_at = it.get_request_from_user?.email_verified_at,
                                        email_verified = it.get_request_from_user?.email_verified,
                                        email_verified_otp = it.get_request_from_user?.email_verified_otp,
                                        coins = it.get_request_from_user?.coins,
                                        user_type = it.get_request_from_user?.user_type,
                                        height = it.get_request_from_user?.height,
                                        user_intrested_in = it.get_request_from_user?.user_intrested_in,
                                        hobbies = it.get_request_from_user?.hobbies,
                                        created_at = it.get_request_from_user?.created_at,
                                        updated_at = it.get_request_from_user?.updated_at,
                                        distance = it.get_request_from_user?.distance,
                                        user_educations = it.get_request_from_user?.user_educations,
                                        user_looking_for = it.get_request_from_user?.user_looking_for,
                                        user_dietary_lifestyle = it.get_request_from_user?.user_dietary_lifestyle,
                                        user_pets = it.get_request_from_user?.user_pets,
                                        user_arts = it.get_request_from_user?.user_arts,
                                        user_language = it.get_request_from_user?.user_language,
                                        user_interests = it.get_request_from_user?.user_interests,
                                        user_drink = it.get_request_from_user?.user_drink,
                                        user_drugs = it.get_request_from_user?.user_drugs,
                                        user_horoscope = it.get_request_from_user?.user_horoscope,
                                        user_religion = it.get_request_from_user?.user_religion,
                                        user_political_leaning = it.get_request_from_user?.user_political_leaning,
                                        user_relationship_status = it.get_request_from_user?.user_relationship_status,
                                        user_life_style = it.get_request_from_user?.user_life_style,
                                        user_first_date_ice_breaker = it.get_request_from_user?.user_first_date_ice_breaker,
                                        user_covid_vaccine = it.get_request_from_user?.user_covid_vaccine,
                                        user_smoking = it.get_request_from_user?.user_smoking,
                                        user_images = it.get_request_from_user?.user_images,
                                    ),
                                    user_likes_from = it.user_likes_from,
                                )
                            }

                            newList.addAll(abList ?: listOf())
                            /* newList.addAll(abList?.sortedByDescending {
                                 it.get_request_to_user?.created_at
                             } ?: listOf())*/
                             setOldList(newList)

                        }

                        if (apiData.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }


    }

    val Listner = AcceptedChatAdepter.onClick { data, position ->
        val intent = Intent(requireContext(), PersonalChatActivity::class.java)
        intent.putExtra(
            "currentUserId",
            viewModelTop?.tempUserDataObject?.id?.toIntOrNull() ?: ""
        )
        intent.putExtra("otherUserId", data.get_request_to_user?.id)
        intent.putExtra(
            "otherUserImage",
            data.get_request_to_user?.user_images?.firstOrNull()?.url ?: ""
        )
        intent.putExtra("otherUserName", data.get_request_to_user?.first_name ?: "")
        intent.putExtra("matchId", data.match_id?.toIntOrNull())
        intent.putExtra("requestmsg", data.invite_msg ?: 0)
        intent.putExtra("privateChatTime", data.created_at ?: 0)
        intent.putExtra("receiver", data.request_status ?: 0)
        intent.putExtra("IsRead", "private_chat")

        startActivity(intent)
    }


    private fun setOldList(listData: List<PrivateUserSendChatDataList>) {

        if (listData.isEmpty()) {
            binding.recyGroup.visibility = View.GONE
            binding.emptyimg.visibility = View.VISIBLE
        } else {

            binding.recyGroup.visibility = View.VISIBLE
            binding.emptyimg.visibility = View.GONE

        }

        lifecycleScope.launch {
            requireActivity().dataStoreGetUserData().catch { it.printStackTrace() }
                .firstOrNull {
                    binding.recyGroup.adapter =
                        AcceptedChatAdepter(
                            requireActivity(),
                            Listner,
                            it.id.toIntOrNull() ?: 0,
                            listData
                        )
                    true
                }
        }


    }

    companion object {
        @JvmStatic
        fun newInstance(viewModel: GetRequestedChatViewModel): AcceptedFragment {
            viewModelTop = viewModel
            return AcceptedFragment()
        }
    }

}

private var viewModelTop: GetRequestedChatViewModel? = null

