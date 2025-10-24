package com.foreverinlove.network.response

data class ChildHobbyListResponse (val status:Int?,val message:String?,val data:List<ChildHobbyData>?)

data class ChildHobbyData (val id:Int?,val hobbie_id:Int?,val sub_hobbie_name:String?,val data:List<HobbiesListData>,val maxCount:Int)
