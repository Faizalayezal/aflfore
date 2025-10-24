package com.foreverinlove.groupvideocall

data class GroupVideoCallUserList(
    val uId: Int,
    val username: String,
    val isAudioEnable: Boolean = true,
    val isVideoEnable: Boolean = true,
)
