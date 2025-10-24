package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.PrivateChatListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RejectedPrivateChatViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null
    fun start() {
        tempUserDataObject = null
        viewModelScope.launch {
            context.dataStoreGetUserData().collect{
                if(tempUserDataObject==null){
                    tempUserDataObject=it

                }

            }
        }
    }

  //  'requested','accepted','rejected'
    sealed class RejectedRequestResponseEvent{
        class Success(val result: PrivateChatListResponse): RejectedRequestResponseEvent()
        class Failure(val errorText:String): RejectedRequestResponseEvent()
        object Loading: RejectedRequestResponseEvent()
        object Empty: RejectedRequestResponseEvent()
    }

    private val _rejectConversion = MutableStateFlow<RejectedRequestResponseEvent>(
        RejectedRequestResponseEvent.Empty)
    val rejectConversion: StateFlow<RejectedRequestResponseEvent> = _rejectConversion

    fun callApiRejectedData(uid:String) {
        viewModelScope.launch {
            _rejectConversion.value = RejectedRequestResponseEvent.Loading
            when(val quotesResponse = repository.rejectPrivateChat(tempUserDataObject?.token?:"",uid)) {
                is Resource.Error -> _rejectConversion.value =
                    RejectedRequestResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _rejectConversion.value = RejectedRequestResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }




}