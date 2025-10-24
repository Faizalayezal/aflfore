package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.LikesListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhoLikeMeViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application){
   private var tempUserDataObject: TempUserDataObject? = null

   fun start(flow: String) {
       viewModelScope.launch {
           context.dataStoreGetUserData()
               .catch{
                   it.printStackTrace()
               }
               .collect {

                   tempUserDataObject = it

                   when(flow){
                       "WhoLike"->callApiData()
                       "MyLike"->callApiMyLikes()
                   }

               }
       }
   }

    sealed class ViewedMeResponseEvent{
        class Success(val result: LikesListResponse): ViewedMeResponseEvent()
        class Failure(val errorText:String): ViewedMeResponseEvent()
        object Loading: ViewedMeResponseEvent()
        object Empty: ViewedMeResponseEvent()
    }

    //mutuble value edit mate on the sport
    private val _viewedMeListConversion = MutableStateFlow<ViewedMeResponseEvent>(ViewedMeResponseEvent.Empty)
    val viewedMeListConversion: StateFlow<ViewedMeResponseEvent> = _viewedMeListConversion

    fun callApiData() {
        viewModelScope.launch {
            _viewedMeListConversion.value = ViewedMeResponseEvent.Loading
            when(val quotesResponse = repository.getGetLikeMeList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _viewedMeListConversion.value =
                    ViewedMeResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _viewedMeListConversion.value = ViewedMeResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }
//getMyViewProfileList
    private fun callApiMyLikes() {
    viewModelScope.launch {
        _viewedMeListConversion.value = ViewedMeResponseEvent.Loading
        when(val quotesResponse = repository.getMyLikeList(tempUserDataObject?.token?:"")) {
            is Resource.Error -> _viewedMeListConversion.value =
                ViewedMeResponseEvent.Failure(quotesResponse.message!!)
            is Resource.Success -> {
                val quote = quotesResponse.data!!
                _viewedMeListConversion.value = ViewedMeResponseEvent.Success(
                    quote
                )
            }
        }

    }
    }
}