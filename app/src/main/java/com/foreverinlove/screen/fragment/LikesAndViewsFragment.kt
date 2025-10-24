package com.foreverinlove.screen.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentLikesAndViewsBinding
import com.foreverinlove.screen.activity.MyLikesActivity
import com.foreverinlove.screen.activity.ReviewLaterActivity
import com.foreverinlove.screen.activity.WhoLikesMeActivity
import com.foreverinlove.screen.activity.WhoViewMeActivity


class LikesAndViewsFragment : BaseFragment(R.layout.fragment_likes_and_views) {

    private lateinit var binding: FragmentLikesAndViewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLikesAndViewsBinding.bind(view)
        screenOpened( "LikeAndViewTab")

        binding.btnWhoLikesMe.setOnClickListener {
            startActivity(Intent(requireContext(), WhoLikesMeActivity::class.java))
        }

        binding.btnWhohasviewedme.setOnClickListener {
            startActivity(Intent(requireContext(), WhoViewMeActivity::class.java))
        }
        binding.btnSavedLikes.setOnClickListener {
            startActivity(Intent(requireContext(), MyLikesActivity::class.java).putExtra("likesFlow",true))

        }
        binding.btnSavedSuperLikes.setOnClickListener {
            startActivity(Intent(requireContext(), MyLikesActivity::class.java).putExtra("likesFlow",false))
        }
        binding.btnReview.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewLaterActivity::class.java))
        }

    }


}