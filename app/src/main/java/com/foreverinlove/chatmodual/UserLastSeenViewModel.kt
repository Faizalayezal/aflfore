package com.foreverinlove.chatmodual

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLastSeenViewModel @Inject constructor(application: Application, val repository: MainRepository
) : BaseViewModel(application) {

    private lateinit var database: DatabaseReference

    var userId=""
    fun start(){
        database = FirebaseDatabase.getInstance().reference
        viewModelScope.launch {
            context.dataStoreGetUserData().collect {
                userId=it.id.toString()

                if(isStartedEarly){
                    isStartedEarly=false
                    makeOnline()
                }
            }
        }
    }

    fun makeOffline(){
        val ab=OnlineStatusObj("0",System.currentTimeMillis().toString())
        database.child("onlineStatus").child(userId).setValue(ab)
    }

    private var isStartedEarly=false

    fun makeOnline(){

        if(userId==""){
            isStartedEarly=true
            return
        }

        val ab=OnlineStatusObj("1",System.currentTimeMillis().toString())
        database.child("onlineStatus").child(userId).setValue(ab)
    }

    fun removeAPopup(screenId:String)=GlobalScope.launch {
        val token = context.dataStoreGetUserData().firstOrNull()?.token?:""
        repository.readPopupData(token, screenId = screenId)
    }

}


data class OnlineStatusObj(
    var status:String?=null,
    var lastSeen:String?=null,
)