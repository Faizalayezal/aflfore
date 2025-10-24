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
class ConfrimPrivateChatViewModel @Inject constructor(
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

    sealed class ConfirmRequestResponseEvent{
        class Success(val result: PrivateChatListResponse): ConfirmRequestResponseEvent()
        class Failure(val errorText:String): ConfirmRequestResponseEvent()
        object Loading: ConfirmRequestResponseEvent()
        object Empty: ConfirmRequestResponseEvent()
    }

    private val _confirmConversion = MutableStateFlow<ConfirmRequestResponseEvent>(
        ConfirmRequestResponseEvent.Empty)
    val confirmConversion: StateFlow<ConfirmRequestResponseEvent> = _confirmConversion

    fun callApiConfirmData(uid:String) {
        viewModelScope.launch {
            _confirmConversion.value = ConfirmRequestResponseEvent.Loading
            when(val quotesResponse = repository.confirmPrivateChat(tempUserDataObject?.token?:"",uid)) {
                is Resource.Error -> _confirmConversion.value =
                    ConfirmRequestResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _confirmConversion.value = ConfirmRequestResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }




}