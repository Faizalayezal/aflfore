package com.foreverinlove.screen.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.JoinAdapter
import com.foreverinlove.databinding.FragmentJoinedBinding
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.groupchatflow.GroupChatActivity
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.RoomList
import com.foreverinlove.screen.activity.SubscriptionPlanActivity
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetRoomListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "JoinedFragment"
@AndroidEntryPoint
class JoinedFragment : Fragment(R.layout.fragment_joined) {



    private lateinit var binding: FragmentJoinedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJoinedBinding.bind(view)



        lifecycleScope.launch {
            viewModelTop?.joinListConversion?.collect {
                when (it) {
                    GetRoomListViewModel.JoinListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetRoomListViewModel.JoinListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                       // requireActivity().showToast(it.errorText)

                    }
                    is GetRoomListViewModel.JoinListResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }
                    is GetRoomListViewModel.JoinListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        Log.d(TAG, "onViewCreated: testFDlowAA>>")
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {
                                binding.recyGroup.visibility=View.GONE
                                binding.emptyimg.visibility=View.VISIBLE
                            } else {
                                binding.recyGroup.visibility=View.VISIBLE
                                binding.emptyimg.visibility=View.GONE
                                groupuselist(it.result.data)
                            }
                        }else if(it.result.message=="Please Upgrade Your Account."){
                            sucessfullApplyed()
                        }
                        else if (it.result.status == -2) {
                           requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }



    }

    // constructor vgr call kri ske
    companion object {
        @JvmStatic
        fun newInstance(viewModel: GetRoomListViewModel): JoinedFragment {
            viewModelTop=viewModel
            return JoinedFragment()
        }
    }


    private val listTop = ArrayList<RoomList>()
    private lateinit var groupChatAdapter: JoinAdapter
    private fun groupuselist(listData: List<RoomList>) {
        listTop.clear()
        listTop.addAll(listData)

        groupChatAdapter = JoinAdapter(requireActivity(),multiListner, listTop)
        binding.recyGroup.adapter = groupChatAdapter
    }

    private var isFirstTime = true
    override fun onResume() {
        super.onResume()
        if (isFirstTime) {
            isFirstTime = false
        } else {
            viewModelTop?.callApiJoinListData()
        }

    }


    override fun onDestroy() {
        viewModelTop = null
        super.onDestroy()
    }
    private val multiListner=object:JoinAdapter.onClick{
        override fun openChat(roomList: RoomList) {
            val intent=Intent(requireContext(),GroupChatActivity::class.java)
            intent.putExtra("roomData",roomList)
            startActivity(intent)

        }

    }


    private fun sucessfullApplyed() {
        val dialog = Dialog(requireContext(), R.style.successfullDailog)
        dialog.setContentView(R.layout.dailoggroupchat)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.sucessaplaytransperent
                )
            )
        )

        Glide.with(requireContext()).load(R.mipmap.groupempty)
            .into(dialog.findViewById(R.id.imageView13))


        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), SubscriptionPlanActivity::class.java))
        }

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()


        }
        dialog.setOnDismissListener {
            try {
                requireActivity().onBackPressed()

            }catch (e: Exception){
                e.printStackTrace()

            }
        }


        dialog.show()

    }




}
private var viewModelTop: GetRoomListViewModel? = null
