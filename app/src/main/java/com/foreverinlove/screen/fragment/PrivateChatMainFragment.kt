package com.foreverinlove.screen.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.foreverinlove.*
import com.foreverinlove.chatflow.ChatViewModel
import com.foreverinlove.databinding.FragmentPrivateChatBinding
import com.foreverinlove.viewmodels.GetRequestedChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@AndroidEntryPoint
class PrivateChatMainFragment : Fragment(R.layout.fragment_private_chat) {

    private lateinit var binding: FragmentPrivateChatBinding
    var selectedItem = 0
    private val viewModel: GetRequestedChatViewModel by viewModels()
    private val myChatViewModel: ChatViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrivateChatBinding.bind(view)

        lifecycleScope.launchWhenResumed {
            while (isActive){
                viewModel.start()
                delay(1000*10)
            }

        }

        binding.apply {
            llJoined.setOnClickListener {
                selectedItem = 0
                pager.currentItem = selectedItem
                setSelectedTab(selectedItem)
            }
            llSuites.setOnClickListener {
                selectedItem = 1
                pager.currentItem = selectedItem
                setSelectedTab(selectedItem)
            }
            llRequested.setOnClickListener {
                selectedItem = 2
                pager.currentItem = selectedItem
                setSelectedTab(selectedItem)
            }
            imgPrivateChat.setOnClickListener {
                requireActivity().onBackPressed()
            }

            setSelectedTab(selectedItem)
            pager.currentItem = selectedItem

            val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
            pager.offscreenPageLimit = 2

            pager.adapter = pagerAdapter
            pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    selectedItem = position
                    setSelectedTab(selectedItem)
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
        Constant.matchIdListener = object : OnDataMatchIdListener {
            override fun onOnDataMatchIdListenerSelected(data: String) {
                Log.d("TAG", "onOnDataMatchIdListenerSelected: " + data)
                myChatViewModel.readmsg(data, "private_chat")

            }

        }
    }


    private fun setSelectedTab(selectedItem: Int) {
        binding.apply {
            when (selectedItem) {
                0 -> {

                    binding.joinIndector.visibility = View.VISIBLE
                    binding.AvailableIndector.visibility = View.GONE
                    binding.RequestedIndector.visibility = View.GONE
                }
                1 -> {
                    binding.joinIndector.visibility = View.GONE
                    binding.AvailableIndector.visibility = View.VISIBLE
                    binding.RequestedIndector.visibility = View.GONE
                }
                2 -> {
                    binding.joinIndector.visibility = View.GONE
                    binding.AvailableIndector.visibility = View.GONE
                    binding.RequestedIndector.visibility = View.VISIBLE
                }
            }
        }
    }


    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 3

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AcceptedFragment.newInstance(viewModel)
                1 -> ReceivedFragment.newInstance(viewModel)
                2 -> PendingFragment.newInstance(viewModel)
                else -> AcceptedFragment.newInstance(viewModel)
            }
        }
    }

}