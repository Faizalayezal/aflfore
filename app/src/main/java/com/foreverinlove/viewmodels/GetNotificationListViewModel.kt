package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.Notificationresponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetNotificationListViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null
    fun start() {
        tempUserDataObject = null
        viewModelScope.launch {
            context.dataStoreGetUserData().collect {
                if (tempUserDataObject == null) {
                    tempUserDataObject = it
                    callNotificationApiData()

                }

            }
        }
    }

    sealed class NotificationListResponseEvent {
        class Success(val result: Notificationresponse) : NotificationListResponseEvent()
        class Failure(val errorText: String) : NotificationListResponseEvent()
        object Loading : NotificationListResponseEvent()
        object Empty : NotificationListResponseEvent()
    }

    private val _notificationListConversion = MutableStateFlow<NotificationListResponseEvent>(
        NotificationListResponseEvent.Empty
    )
    val notificationListConversion: StateFlow<NotificationListResponseEvent> = _notificationListConversion

    fun callNotificationApiData() {
        viewModelScope.launch {
            _notificationListConversion.value = NotificationListResponseEvent.Loading
            when (val quotesResponse = repository.getNotification(tempUserDataObject?.token ?: "")) {
                is Resource.Error -> _notificationListConversion.value =
                    NotificationListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _notificationListConversion.value = NotificationListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }


}


