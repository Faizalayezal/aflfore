package com.foreverinlove.screen.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant
import com.foreverinlove.OnDataMatchIdListener
import com.foreverinlove.R
import com.foreverinlove.adapter.ItemChatListAdapter
import com.foreverinlove.adapter.UserChatListAdapter
import com.foreverinlove.chatflow.ChatViewModel
import com.foreverinlove.chatflow.PersonalChatActivity
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentNewConverationBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.MessageConversationList
import com.foreverinlove.network.response.OldMessageData
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.ConverationViewmodel
import com.foreverinlove.viewmodels.ReportUnmatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewConverationFragment : BaseFragment(R.layout.fragment_new_converation) {
    private var binding: FragmentNewConverationBinding? = null


    private val viewModel: ConverationViewmodel by viewModels()
    private val viewModelReportUnmatch: ReportUnmatchViewModel by viewModels()
    var currantPlanData = true
    var otherPlanData = true

    private val myChatViewModel: ChatViewModel by viewModels()
    private var matchId: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewConverationBinding.bind(view)
        screenOpened("MessageTab")

        requireActivity().intent?.apply {
            matchId = getStringExtra("matchIds") ?: ""
        }


        lifecycleScope.launch {
            viewModel.conversionListConversion.collect {
                when (it) {
                    ConverationViewmodel.ConversationResponseEvent.Empty -> Utility.hideProgressBar()
                    is ConverationViewmodel.ConversationResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }

                    is ConverationViewmodel.ConversationResponseEvent.Loading -> {
                        Utility.hideProgressBar()

                    }

                    is ConverationViewmodel.ConversationResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data?.conversation_not_started_array?.isEmpty() == true) {
                                preUserChatList(listOf())

                            } else {
                                preUserChatList(
                                    it.result.data?.conversation_not_started_array ?: listOf()
                                )
                            }

                            if (it.result.data?.conversation_not_started_array?.firstOrNull()?.order_active == null) {
                                otherPlanData = false
                            }
                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.newMessagesUserListConversion.collect {
                when (it) {
                    ConverationViewmodel.NewMessagesUserListEvent.Empty -> Utility.hideProgressBar()
                    is ConverationViewmodel.NewMessagesUserListEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }

                    is ConverationViewmodel.NewMessagesUserListEvent.Loading -> {
                        Utility.hideProgressBar()
                    }

                    is ConverationViewmodel.NewMessagesUserListEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data?.conversationStartedArray.isNullOrEmpty()) {
                                binding?.txtnewmsg?.visibility = View.GONE
                                setOldList(listOf())

                            } else {
                                setOldList(it.result.data?.conversationStartedArray?.sortedByDescending {
                                    it.created_at
                                } ?: listOf())
                            }
                            if (it.result.data?.order!=null) {
                                currantPlanData = false
                                Log.d("TAG", "onViewCreated1235: "+1545)
                            }
                            if (it.result.data?.conversationStartedArray?.firstOrNull()?.order_active!=null) {
                                otherPlanData = false
                                Log.d("TAG", "onViewCreated1235: "+130)

                            }

                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModelReportUnmatch.reportUnMatchConversion.collect {
                when (it) {
                    ReportUnmatchViewModel.ResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is ReportUnmatchViewModel.ResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                    }

                    ReportUnmatchViewModel.ResponseEvent.Loading -> {
                        requireActivity().showProgressBar()
                    }

                    is ReportUnmatchViewModel.ResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        } else {
                            viewModel.getOldUserMessageList()

                        }
                    }
                }
            }
        }


    }


    private val multiListener = object : UserChatListAdapter.onClick {
        override fun openDetail(data: OldMessageData, position: Int) {
            val intent = Intent(requireContext(), PersonalChatActivity::class.java)
            intent.putExtra("currentUserId", (viewModel.tempUserDataObject?.id)?.toIntOrNull() ?: 0)
            intent.putExtra("otherUserId", data.user_id ?: 0)
            intent.putExtra("otherUserImage", data.user_image_url?.firstOrNull()?.url ?: "")
            intent.putExtra("otherUserName", data.user_name ?: "")
            intent.putExtra("matchId", data.match_id ?: 0)
            intent.putExtra("planStatus", currantPlanData)
            intent.putExtra("otherplanStatus", otherPlanData)
            intent.putExtra("IsRead", "normal_chat")
            startActivity(intent)

        }


    }

    private val userListener = object : ItemChatListAdapter.onClick {
        override fun openDetail(data: MessageConversationList, position: Int) {
            val intent = Intent(requireContext(), PersonalChatActivity::class.java)
            intent.putExtra("currentUserId", (viewModel.tempUserDataObject?.id)?.toIntOrNull() ?: 0)
            intent.putExtra("otherUserId", data.user_id ?: 0)
            intent.putExtra("otherUserImage", data.image?.firstOrNull()?.url ?: "")
            intent.putExtra("otherUserName", data.user_name ?: "")
            intent.putExtra("planStatus", currantPlanData)
            intent.putExtra("otherplanStatus", otherPlanData)
            intent.putExtra("matchId", data.match_id ?: 0)
            intent.putExtra("IsRead", "normal_chat")
            startActivity(intent)
        }


    }

    private val itemChatListAdapter: ItemChatListAdapter by lazy {
        ItemChatListAdapter(requireActivity(), userListener, ::isListEmptyListener).apply {
            binding?.newuserRecy?.adapter = this
        }
    }

    private fun isListEmptyListener(isEmpty: Boolean) {
        if (isEmpty) {
            binding?.newuserRecy?.visibility = View.GONE
        } else {
            binding?.newuserRecy?.visibility = View.VISIBLE
        }
    }

    private fun preUserChatList(data: List<MessageConversationList>) {
        itemChatListAdapter.setListData(data)
        viewModel.getOnlineStatusOfUsers(data) {
            itemChatListAdapter.setListData(it)
            EmptyImage()
        }
    }

    private fun EmptyImage() {

        val abc = binding?.userchatRecy?.adapter?.itemCount
        val abc2 = binding?.newuserRecy?.adapter?.itemCount



        if ((abc == null || abc == 0) && (abc2 == null || abc2 == 0)) {
            binding?.imgEmpty?.visibility = View.VISIBLE
            binding?.txtnewmsg?.visibility = View.GONE
            binding?.userchatRecy?.visibility = View.GONE
            binding?.newuserRecy?.visibility = View.GONE

        } else {
            binding?.imgEmpty?.visibility = View.GONE
            binding?.userchatRecy?.visibility = View.VISIBLE
            binding?.newuserRecy?.visibility = View.VISIBLE

        }
    }

    private fun setOldList(data: List<OldMessageData>) {

        lifecycleScope.launch {
            requireActivity().dataStoreGetUserData().catch { it.printStackTrace() }
                .firstOrNull {
                    binding?.userchatRecy?.adapter =
                        UserChatListAdapter(
                            requireActivity(),
                            multiListener,
                            it.id.toIntOrNull() ?: 0,
                            data
                        )
                    true
                }
            EmptyImage()
        }


    }

    override fun onPause() {
        super.onPause()
        loopApiCall(false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
        Constant.matchIdListener = object : OnDataMatchIdListener {
            override fun onOnDataMatchIdListenerSelected(data: String) {
                Log.d("TAG", "onOnDataMatchIdListenerSelected: " + data)
                myChatViewModel.readmsg(data, "normal_chat")

            }

        }

        loopApiCall(true)
    }

    //refresh page mate courotins job no use thay

    private val apiCallDelaySec = 5
    private var job: Job? = null
    private fun loopApiCall(isStart: Boolean) {
        if (job?.isActive == true) job?.cancel()

        if (isStart) {
            job = lifecycleScope.launch {
                delay((apiCallDelaySec * 1000).toLong())
                viewModel.start()
                delay(5000)
                loopApiCall(true)
            }
        }
    }


}