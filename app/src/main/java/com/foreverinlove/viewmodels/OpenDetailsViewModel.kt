package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.*
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenDetailsViewModel @Inject constructor(
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

    sealed class OpenDetailsResponseEvent{
        class Success(val result: UserDetailsresponse): OpenDetailsResponseEvent()
        class Failure(val errorText:String): OpenDetailsResponseEvent()
        object Loading: OpenDetailsResponseEvent()
        object Empty: OpenDetailsResponseEvent()
    }

    private val _opneDetailsConversion = MutableStateFlow<OpenDetailsResponseEvent>(
        OpenDetailsResponseEvent.Empty)
    val opneDetailsConversion: StateFlow<OpenDetailsResponseEvent> = _opneDetailsConversion

    fun callApiOpenDetails(uid:String) {
        viewModelScope.launch {
            _opneDetailsConversion.value = OpenDetailsResponseEvent.Loading
            when(val quotesResponse = repository.openUserDetails(tempUserDataObject?.token?:"",uid)) {
                is Resource.Error -> _opneDetailsConversion.value =
                    OpenDetailsResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _opneDetailsConversion.value = OpenDetailsResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }




}