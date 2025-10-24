package com.foreverinlove.screen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.PeddingChatAdepter
import com.foreverinlove.databinding.FragmentPendingBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.PrivateUserSendChatDataList
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetRequestedChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingFragment : Fragment(R.layout.fragment_pending) {
    private lateinit var binding: FragmentPendingBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPendingBinding.bind(view)

        Glide.with(requireContext()).load(R.mipmap.padding).into(binding.emptyimg)

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

                            if (it.result.data?.request_sent_users.isNullOrEmpty()) {
                                binding.recyGroup.visibility = View.GONE
                                binding.emptyimg.visibility = View.VISIBLE
                            } else {
                                binding.recyGroup.visibility = View.VISIBLE
                                binding.emptyimg.visibility = View.GONE
                            }

                            Pendinglist(it.result.data?.request_sent_users ?: listOf())

                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }

    }

    private val listTop = ArrayList<PrivateUserSendChatDataList>()
    private lateinit var receiveChatAdepter: PeddingChatAdepter
    private fun Pendinglist(listData: List<PrivateUserSendChatDataList>) {
        listTop.clear()
        listTop.addAll(listData)

        receiveChatAdepter = PeddingChatAdepter(requireActivity(), listTop)
        binding.recyGroup.adapter = receiveChatAdepter
    }

    companion object {
        @JvmStatic
        fun newInstance(viewModel: GetRequestedChatViewModel): PendingFragment {
            viewModelTop = viewModel
            return PendingFragment()
        }
    }

    override fun onDestroy() {
        viewModelTop = null
        super.onDestroy()
    }
}

private var viewModelTop: GetRequestedChatViewModel? = null
