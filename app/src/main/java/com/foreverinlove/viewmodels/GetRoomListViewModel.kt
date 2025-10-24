package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.GetRequestedListResponse
import com.foreverinlove.network.response.GetRoomListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetRoomListViewModel @Inject constructor(
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
                    callAvailableRoomApiData()
                    callApiJoinListData()
                    callApiRequestListData()
                }

            }
        }
    }

    sealed class ReportListResponseEvent{
        class Success(val result: GetRoomListResponse): ReportListResponseEvent()
        class Failure(val errorText:String): ReportListResponseEvent()
        object Loading: ReportListResponseEvent()
        object Empty: ReportListResponseEvent()
    }

    private val _reportListConversion = MutableStateFlow<ReportListResponseEvent>(
        ReportListResponseEvent.Empty)
    val reportListConversion: StateFlow<ReportListResponseEvent> = _reportListConversion

     private fun callAvailableRoomApiData() {
        viewModelScope.launch {
            _reportListConversion.value = ReportListResponseEvent.Loading
            when(val quotesResponse = repository.getRoomList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _reportListConversion.value =
                    ReportListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {

                    val quote = quotesResponse.data!!
                    _reportListConversion.value = ReportListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }



    sealed class RequestResponseEvent{
        class Success(val result: GetRequestedListResponse): RequestResponseEvent()
        class Failure(val errorText:String): RequestResponseEvent()
        object Loading: RequestResponseEvent()
        object Empty: RequestResponseEvent()
    }

    private val _requestConversion = MutableStateFlow<RequestResponseEvent>(
        RequestResponseEvent.Empty)
    val requestConversion: StateFlow<RequestResponseEvent> = _requestConversion


    fun callRequestedApiData(uid:String) {
        viewModelScope.launch {
            _requestConversion.value = RequestResponseEvent.Loading
            when(val quotesResponse = repository.RequestedRoom(tempUserDataObject?.token?:"",uid)) {
                is Resource.Error -> _requestConversion.value =
                    RequestResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    if(quote.status==1 || quote.status==-2){
                        _requestConversion.value = RequestResponseEvent.Success(
                            quote
                        )
                    }else{
                        _requestConversion.value =
                            RequestResponseEvent.Failure(quotesResponse.message?:"")
                    }

                }
            }

        }
    }





    sealed class JoinListResponseEvent{
        class Success(val result: GetRoomListResponse): JoinListResponseEvent()
        class Failure(val errorText:String): JoinListResponseEvent()
        object Loading: JoinListResponseEvent()
        object Empty: JoinListResponseEvent()
    }

    private val _joinListConversion = MutableStateFlow<JoinListResponseEvent>(
        JoinListResponseEvent.Empty)
    val joinListConversion: StateFlow<JoinListResponseEvent> = _joinListConversion

    fun callApiJoinListData() {
        viewModelScope.launch {
            _joinListConversion.value = JoinListResponseEvent.Loading
            when(val quotesResponse = repository.getJoinList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _joinListConversion.value =
                    JoinListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _joinListConversion.value = JoinListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }


    sealed class RequestListResponseEvent{
        class Success(val result: GetRoomListResponse): RequestListResponseEvent()
        class Failure(val errorText:String): RequestListResponseEvent()
        object Loading: RequestListResponseEvent()
        object Empty: RequestListResponseEvent()
    }

    private val _requestListConversion = MutableStateFlow<RequestListResponseEvent>(
        RequestListResponseEvent.Empty)
    val requestListConversion: StateFlow<RequestListResponseEvent> = _requestListConversion

    fun callApiRequestListData() {
        viewModelScope.launch {
            _requestListConversion.value = RequestListResponseEvent.Loading
            when(val quotesResponse = repository.getRequestList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _requestListConversion.value =
                    RequestListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _requestListConversion.value = RequestListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }

}