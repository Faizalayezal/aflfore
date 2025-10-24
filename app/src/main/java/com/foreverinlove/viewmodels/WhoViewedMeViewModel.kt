package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.ViewedMeListResponse
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
class WhoViewedMeViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch{
                    it.printStackTrace()
                }
                .collect {

                    tempUserDataObject = it
                    callApiData()

                }
        }
    }

    sealed class ViewedMeResponseEvent{
        class Success(val result: ViewedMeListResponse): ViewedMeResponseEvent()
        class Failure(val errorText:String): ViewedMeResponseEvent()
        object Loading: ViewedMeResponseEvent()
        object Empty: ViewedMeResponseEvent()
    }

    private val _viewedMeListConversion = MutableStateFlow<ViewedMeResponseEvent>(
        ViewedMeResponseEvent.Empty)
    val viewedMeListConversion: StateFlow<ViewedMeResponseEvent> = _viewedMeListConversion

    private fun callApiData() {
        viewModelScope.launch {
            _viewedMeListConversion.value = ViewedMeResponseEvent.Loading
            when(val quotesResponse = repository.getGetViewProfileList(tempUserDataObject?.token?:"")) {
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