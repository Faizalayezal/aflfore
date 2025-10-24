package com.foreverinlove.viewmodels

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
class UserLastSeenFragmentViewModel @Inject constructor(application: Application, val repository: MainRepository
) : BaseViewModel(application) {


    fun removeAPopup(screenId:String)= GlobalScope.launch {
        val token = context.dataStoreGetUserData().firstOrNull()?.token?:""
        repository.readPopupData(token, screenId = screenId)
    }

}