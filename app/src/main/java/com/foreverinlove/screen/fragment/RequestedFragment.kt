package com.foreverinlove.screen.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.R
import com.foreverinlove.adapter.RequestChatAdapter
import com.foreverinlove.databinding.FragmentRequestedBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.RoomList
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetRoomListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestedFragment : Fragment(R.layout.fragment_requested) {


    private lateinit var binding: FragmentRequestedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentRequestedBinding.bind(view)



        lifecycleScope.launch {
            viewModelTop?.requestListConversion?.collect {
                when (it) {
                    GetRoomListViewModel.RequestListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetRoomListViewModel.RequestListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }
                    is GetRoomListViewModel.RequestListResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }
                    is GetRoomListViewModel.RequestListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {
                                binding.recyRequested.visibility=View.GONE
                                binding.emptyimg.visibility=View.VISIBLE
                            } else {
                                binding.recyRequested.visibility=View.VISIBLE
                                binding.emptyimg.visibility=View.GONE
                                requestechatlist(it.result.data)
                            }
                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }


    }

    private val listTop = ArrayList<RoomList>()
    private lateinit var requestChatAdapter: RequestChatAdapter
    private fun requestechatlist(listData: List<RoomList>) {
        listTop.clear()
        listTop.addAll(listData)

        requestChatAdapter = RequestChatAdapter(requireActivity(), listTop)
        binding.recyRequested.adapter = requestChatAdapter
    }

    // constructor vgr call kri ske

    companion object {
        @JvmStatic
        fun newInstance(viewModel:GetRoomListViewModel): RequestedFragment {
             viewModelTop = viewModel
            return RequestedFragment()
        }
    }

    override fun onDestroy() {
       // binding=null
        viewModelTop = null
        super.onDestroy()
    }


}
private var viewModelTop: GetRoomListViewModel?=null