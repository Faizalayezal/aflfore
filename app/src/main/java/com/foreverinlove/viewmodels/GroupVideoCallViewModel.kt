package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.DefaultMainRepository
import com.foreverinlove.network.response.BaseResponse
import com.foreverinlove.network.response.ConsumeGroupVideoCallResponse
import com.foreverinlove.network.response.GetMemberList
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupVideoCallViewModel
@Inject constructor(private val repository: DefaultMainRepository, application: Application) :
    BaseViewModel(application) {

    private val _screenState = MutableStateFlow(VideoCallStatus.Ringing)
    val screenStateFlow: StateFlow<VideoCallStatus> = _screenState
    fun changeScreenState(videoCallStatus: VideoCallStatus) {
        _screenState.value = videoCallStatus
    }

    sealed class ApiCallEvent {
        class StartVideoCallSuccess(val result: ConsumeGroupVideoCallResponse) : ApiCallEvent()
        class Failure(val errorText: String) : ApiCallEvent()
        object Loading : ApiCallEvent()
        object Empty : ApiCallEvent()
    }

    private val _apiCallConversion = MutableStateFlow<ApiCallEvent>(
        ApiCallEvent.Empty
    )
    val apiCallConversion: StateFlow<ApiCallEvent> = _apiCallConversion
   /* fun startVideoCall(roomId:String) = viewModelScope.launch {
        _apiCallConversion.value = ApiCallEvent.Loading
        when (val quotesResponse =
            repository.endGroupVideoCall(getUserToken(), roomId)) {
            is Resource.Error ->
                _apiCallConversion.value = ApiCallEvent.Failure(quotesResponse.message!!)
            is Resource.Success -> {
                val quote = quotesResponse.data!!

                if(quote.status == 1){
                    when (val startVideoCallResponse =
                        repository.startGroupVideoCall(getUserToken(), roomId)) {
                        is Resource.Error ->
                            _apiCallConversion.value = ApiCallEvent.Failure(startVideoCallResponse.message!!)
                        is Resource.Success -> {
                            val startCallData = startVideoCallResponse.data!!
                            if(startCallData.status == 1){
                                consumeAgoraData(roomId)
                            }else{
                                _apiCallConversion.value = ApiCallEvent.Failure(startCallData.message?:"")
                            }
                        }
                    }
                }else{
                    _apiCallConversion.value = ApiCallEvent.Failure(quote.message?:"")
                }
            }
        }

    }*/

    fun startVideoCall(roomId:String) = viewModelScope.launch {
        _apiCallConversion.value = ApiCallEvent.Loading
        when (val startVideoCallResponse =
            repository.startGroupVideoCall(getUserToken(), roomId)) {
            is Resource.Error ->
                _apiCallConversion.value = ApiCallEvent.Failure(startVideoCallResponse.message!!)
            is Resource.Success -> {
                val startCallData = startVideoCallResponse.data!!
                if(startCallData.status == 1){
                    consumeAgoraData(roomId)
                }else{
                    _apiCallConversion.value = ApiCallEvent.Failure(startCallData.message?:"")
                }
            }
        }

    }

    fun consumeAgoraData(roomId: String)= viewModelScope.launch {
        _apiCallConversion.value = ApiCallEvent.Loading
        when (val quotesResponse =
            repository.consumeGroupVideoCall(getUserToken(), roomId)) {
            is Resource.Error ->
                _apiCallConversion.value = ApiCallEvent.Failure(quotesResponse.message!!)
            is Resource.Success -> {
                val quote = quotesResponse.data

                _apiCallConversion.value =
                    if(quote?.status == 1) ApiCallEvent.StartVideoCallSuccess(quote)
                else ApiCallEvent.Failure(quote?.message?:"")
            }
        }
    }






    sealed class GetMemberApiCallEvent {
        class Success(val result: GetMemberList) : GetMemberApiCallEvent()
        class Failure(val errorText: String) : GetMemberApiCallEvent()
        object Loading : GetMemberApiCallEvent()
        object Empty : GetMemberApiCallEvent()
    }
    private val _memberListConversion = MutableStateFlow<GetMemberApiCallEvent>(
        GetMemberApiCallEvent.Empty)
    val memberListConversion: StateFlow<GetMemberApiCallEvent> = _memberListConversion



    fun getMemberData(roomId: String) {
        viewModelScope.launch {
            _memberListConversion.value = GetMemberApiCallEvent.Loading
            when(val quotesResponse = repository.getMemberListVideoCall(getUserToken(),roomId)){
                is Resource.Error -> _memberListConversion.value =
                    GetMemberApiCallEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _memberListConversion.value = GetMemberApiCallEvent.Success(
                        quote
                    )
                }
            }

        }
    }



    sealed class UpdateIdsApiCallEvent {
        class Success(val result: BaseResponse) : UpdateIdsApiCallEvent()
        class Failure(val errorText: String) : UpdateIdsApiCallEvent()
        object Loading : UpdateIdsApiCallEvent()
        object Empty : UpdateIdsApiCallEvent()
    }
    private val _updateIdsConversion = MutableStateFlow<UpdateIdsApiCallEvent>(
        UpdateIdsApiCallEvent.Empty)
    val updateIdsConversion: StateFlow<UpdateIdsApiCallEvent> = _updateIdsConversion

    fun updateMemberData(roomId: String,uId:String) {
        viewModelScope.launch {
            _updateIdsConversion.value = UpdateIdsApiCallEvent.Loading
            when(val quotesResponse = repository.updateUidVideoCall(getUserToken(),roomId,uId)){
                is Resource.Error -> _updateIdsConversion.value =
                    UpdateIdsApiCallEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _updateIdsConversion.value = UpdateIdsApiCallEvent.Success(
                        quote
                    )
                }
            }

        }
    }

    suspend fun endAudioCall(token: String, roomId: String) {
        repository.endGroupVideoCall(token, roomId)
    }


    private suspend fun getUserToken(): String =
        context.dataStoreGetUserData().firstOrNull()?.token ?: ""

}

enum class VideoCallStatus { Ringing, Connected }