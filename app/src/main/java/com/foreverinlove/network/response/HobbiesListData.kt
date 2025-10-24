package com.foreverinlove.network.response

data class HobbiesListResponse (val status:Int?,val message:String?,val data:List<HobbiesListData>?)
data class HobbiesListData (val id:Int?,val hobbies:String?)