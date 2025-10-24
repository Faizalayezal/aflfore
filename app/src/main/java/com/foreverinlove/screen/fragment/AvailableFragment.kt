package com.foreverinlove.screen.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.AvailableChatAdapter
import com.foreverinlove.databinding.FragmentAvailableBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.RoomList
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetRoomListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AvailableFragment : Fragment(R.layout.fragment_available), AvailableChatAdapter.onClick {


    private lateinit var binding: FragmentAvailableBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAvailableBinding.bind(view)


        lifecycleScope.launch {
            viewModelTop?.reportListConversion?.collect {
                when (it) {
                    GetRoomListViewModel.ReportListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetRoomListViewModel.ReportListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                      //  requireActivity().showToast(it.errorText)

                    }
                    is GetRoomListViewModel.ReportListResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }
                    is GetRoomListViewModel.ReportListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {
                                binding.recyAvailable.visibility=View.GONE
                                binding.emptyimg.visibility=View.VISIBLE
                            } else {
                                binding.recyAvailable.visibility=View.VISIBLE
                                binding.emptyimg.visibility=View.GONE
                                availbalechatlist(it.result.data)
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
    private lateinit var availableChatAdapter: AvailableChatAdapter
    private fun availbalechatlist(listData: List<RoomList>) {
        listTop.clear()
        listTop.addAll(listData)

        availableChatAdapter = AvailableChatAdapter(requireActivity(),this, listTop)
        binding.recyAvailable.adapter = availableChatAdapter
    }

    // constructor vgr call kri ske

    companion object {
        @JvmStatic
        fun newInstance(viewModel: GetRoomListViewModel): AvailableFragment {
            viewModelTop = viewModel
            return AvailableFragment()
        }
    }

    override fun onDestroy() {
        // binding = null
        viewModelTop = null
        super.onDestroy()
    }



    @SuppressLint("SetTextI18n")
    private fun successfulApplied(roomid:Int, roomname:String) {
        val dialog = Dialog(requireContext(), R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogrequestchat)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.requestransperent
                )
            )
        )

        Glide.with(requireContext()).load(R.mipmap.requestimg)
            .into(dialog.findViewById<ImageView>(R.id.logo))

        dialog.findViewById<AppCompatButton>(R.id.btnJoin).setOnClickListener {

            dialog.dismiss()
            setupReponse()
            viewModelTop?.callRequestedApiData(roomid.toString())



        }
        val gname=dialog.findViewById<TextView>(R.id.txtgchat)
        gname.text= "Do you want to join $roomname?"

        dialog.findViewById<ImageView>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }




        dialog.show()

    }


    private fun setupReponse(){
        lifecycleScope.launch {
            viewModelTop?.requestConversion?.collect {
                when (it) {
                    GetRoomListViewModel.RequestResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetRoomListViewModel.RequestResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }
                    is GetRoomListViewModel.RequestResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }
                    is GetRoomListViewModel.RequestResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                        }
                        if(it.result.message=="Room is full but your request has been reacived"){
                            requireActivity().showToast("Room Is Full But Your Request Has Been Received")

                        }

                        else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }

    }

    override fun openClick(roomId: Int?, roomname: String?) {
        successfulApplied(roomId?:0,roomname.toString())

    }
}
private var viewModelTop:GetRoomListViewModel?=null