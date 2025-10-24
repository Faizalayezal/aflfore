package com.foreverinlove.screen.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.ReceivedChatAdepter
import com.foreverinlove.chatflow.PersonalChatActivity
import com.foreverinlove.databinding.FragmentReceivedBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.PrivateUserReacivedChatDataList
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetRequestedChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReceivedFragment : Fragment(R.layout.fragment_received) {
    private lateinit var binding: FragmentReceivedBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReceivedBinding.bind(view)


        Glide.with(requireContext()).load(R.mipmap.recive).into(binding.emptyimg)

        lifecycleScope.launch {
            viewModelTop?.paddingListConversion?.collect {
                when (it) {
                    GetRequestedChatViewModel.PaddingListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetRequestedChatViewModel.PaddingListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }
                    is GetRequestedChatViewModel.PaddingListResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }
                    is GetRequestedChatViewModel.PaddingListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                            if (it.result.data?.request_reacived_users.isNullOrEmpty()) {
                                binding.recyGroup.visibility = View.GONE
                                binding.emptyimg.visibility = View.VISIBLE
                            }else{
                                binding.recyGroup.visibility = View.VISIBLE
                                binding.emptyimg.visibility = View.GONE
                            }


                            groupuselist(it.result.data?.request_reacived_users ?: listOf())

                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }


    }

    private val Listener = ReceivedChatAdepter.onClick { data, position ->
        val intent = Intent(requireContext(), PersonalChatActivity::class.java)
        intent.putExtra("currentUserId", viewModelTop?.tempUserDataObject?.id)
        intent.putExtra("otherUserId", data.get_request_from_user?.id)
        intent.putExtra("PRIVATEUserId", data.request_from)
        intent.putExtra(
            "otherUserImage",
            data.get_request_from_user?.user_images?.firstOrNull()?.url ?: ""
        )
        intent.putExtra("otherUserName", data.get_request_from_user?.first_name ?: "")
        intent.putExtra("matchId", data.match_id ?: 0)
        intent.putExtra("requestmsg", data.invite_msg ?: "")
        intent.putExtra("privateChatTime", data.created_at ?: 0)
        intent.putExtra("receiver", data.request_status ?: 0)
        startActivity(intent)
    }


    private val listTop = ArrayList<PrivateUserReacivedChatDataList>()
    private lateinit var receiveChatAdepter: ReceivedChatAdepter
    private fun groupuselist(listData: List<PrivateUserReacivedChatDataList>) {
        listTop.clear()

        listTop.addAll(listData)

        receiveChatAdepter = ReceivedChatAdepter(requireActivity(), Listener, listTop)
        binding.recyGroup.adapter = receiveChatAdepter
    }

    companion object {
        @JvmStatic
        fun newInstance(viewModel: GetRequestedChatViewModel): ReceivedFragment {
            viewModelTop = viewModel
            return ReceivedFragment()
        }
    }

    override fun onDestroy() {
        // binding = null
        viewModelTop = null
        super.onDestroy()
    }

}

private var viewModelTop: GetRequestedChatViewModel? = null
