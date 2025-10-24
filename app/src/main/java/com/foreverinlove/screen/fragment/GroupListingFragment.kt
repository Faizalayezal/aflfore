package com.foreverinlove.screen.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentGroupListingBinding
import com.foreverinlove.utility.FragmentExt.loadFragment
import com.foreverinlove.viewmodels.GetRoomListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@AndroidEntryPoint
class GroupListingFragment : BaseFragment(R.layout.fragment_group_listing) {

    private lateinit var binding: FragmentGroupListingBinding
    private val viewModel: GetRoomListViewModel by viewModels()


    var selectedItem = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroupListingBinding.bind(view)

        screenOpened("GroupTab")
        lifecycleScope.launchWhenResumed {
            while (isActive){
                viewModel.start()
                delay(1000*15)
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
            //fargment to fragment screen pass
            imgPrivateChat.setOnClickListener {
                childFragmentManager.loadFragment(
                    PrivateChatMainFragment(),
                    binding.frameLayout.id,
                )

            }

            setSelectedTab(selectedItem)
            pager.currentItem = selectedItem

            val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)

            //viewpagerpagellimit loding ni ave
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


        val callBack=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
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
                0 -> JoinedFragment.newInstance(viewModel)
                1 -> AvailableFragment.newInstance(viewModel)
                2 -> RequestedFragment.newInstance(viewModel)
                else -> JoinedFragment.newInstance(viewModel)
            }
        }
    }





}