package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(application: Application,
                                              private val repository: MainRepository
) : BaseViewModel(application) {
    var tempUserDataObject: TempUserDataObject? = null
    init {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch { it.printStackTrace() }.collect {
                tempUserDataObject=it
            }
        }
    }

    fun addViewToUser(userId:String){
        GlobalScope.launch (Dispatchers.IO){
            repository.getAddViewCount(tempUserDataObject?.token?:"",userId)
        }
    }

}