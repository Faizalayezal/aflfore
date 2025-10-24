package com.foreverinlove.singlevideocall

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.VideoCallResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoCallViewModel @Inject constructor(
    application: Application,
    private val repository: MainRepository
) : BaseViewModel(application) {



    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch { it.printStackTrace() }.collect {
                tempUserDataObject = it
            }
        }
    }

    sealed class VideoCallResponseEvent {
        class Success(val result: VideoCallResponse) : VideoCallResponseEvent()
        class Failure(val errorText: String) : VideoCallResponseEvent()
        object Loading : VideoCallResponseEvent()
        object Empty : VideoCallResponseEvent()
    }

    private val _videoCallConversion = MutableStateFlow<VideoCallResponseEvent>(
        VideoCallResponseEvent.Empty
    )
    val videoCallConversion: StateFlow<VideoCallResponseEvent> = _videoCallConversion

    fun callApiData(userId: String,status: String) {
        GlobalScope.launch {
            _videoCallConversion.value = VideoCallResponseEvent.Loading
            when (val quotesResponse = repository.videoCall(
                tempUserDataObject?.token ?: "",
                userId,
                status
            )) {
                is Resource.Error -> _videoCallConversion.value =
                    VideoCallResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _videoCallConversion.value = VideoCallResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }
}